package com.jong.mikumikureservationbackend.service.auth.impl;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.service.auth.EmailVerificationService;
import com.jong.mikumikureservationbackend.service.auth.MailService;
import com.jong.mikumikureservationbackend.service.auth.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

    private final RedisService redisService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendCode(String email, EmailVerificationPurpose purpose) {
        String prefix = prefix(email, purpose);
        if (redisService.hasKey(prefix + ":cooldown")) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "인증 코드는 60초 뒤에 다시 받을 수 있습니다");
        }

        String code = String.format("%06d", secureRandom.nextInt(1000000));
        redisService.set(prefix + ":code", passwordEncoder.encode(code), CODE_TTL);
        redisService.delete(prefix + ":attempt");
        redisService.delete(prefix + ":verified");
        redisService.set(prefix + ":cooldown", "1", COOLDOWN_TTL);

        // Redis에는 롤백이 없어서 메일이 실패하면 저장한 값을 직접 지운다(재발송 간격도 걸리지 않게)
        try {
            mailService.sendVerificationCode(normalize(email), purpose, code, (int) CODE_TTL.toMinutes());
        } catch (BusinessException e) {
            redisService.delete(prefix + ":code");
            redisService.delete(prefix + ":cooldown");
            throw e;
        }
    }

    @Override
    public void verify(String email, EmailVerificationPurpose purpose, String code) {
        if (purpose == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "purpose: 인증 목적을 입력해 주세요");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "code: 인증 코드를 입력해 주세요");
        }
        String prefix = prefix(email, purpose);
        if (redisService.hasKey(prefix + ":verified")) {
            return;
        }

        String codeHash = redisService.get(prefix + ":code");
        if (codeHash == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "인증 코드를 받지 않았거나 만료되었습니다. 다시 받아 주세요");
        }
        String attemptValue = redisService.get(prefix + ":attempt");
        int attempts = attemptValue == null ? 0 : Integer.parseInt(attemptValue);
        if (attempts >= MAX_ATTEMPTS) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "확인 횟수를 초과했습니다. 인증 코드를 다시 받아 주세요");
        }

        if (!passwordEncoder.matches(code.trim(), codeHash)) {
            redisService.increment(prefix + ":attempt");
            redisService.expire(prefix + ":attempt", CODE_TTL);
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                    "인증 코드가 일치하지 않습니다 (남은 횟수 " + (MAX_ATTEMPTS - attempts - 1) + "회)");
        }
        redisService.set(prefix + ":verified", "1", VERIFIED_TTL);
        redisService.delete(prefix + ":code");
        redisService.delete(prefix + ":attempt");
    }

    @Override
    public void consume(String email, EmailVerificationPurpose purpose) {
        String key = prefix(email, purpose) + ":verified";
        if (!redisService.hasKey(key)) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "이메일 인증을 완료해 주세요");
        }
        redisService.delete(key);
    }

    private String prefix(String email, EmailVerificationPurpose purpose) {
        return "email-verification:" + purpose.name() + ":" + normalize(email);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
