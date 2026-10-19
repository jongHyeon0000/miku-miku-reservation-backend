package com.jong.mikumikureservationbackend.service.auth.impl;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.config.properties.MailProperties;
import com.jong.mikumikureservationbackend.service.auth.MailService;
import com.jong.mikumikureservationbackend.service.common.MailHtmlTemplateRenderer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final MailHtmlTemplateRenderer mailHtmlTemplateRenderer;

    @Override
    public void sendVerificationCode(String to, EmailVerificationPurpose purpose, String code, int expireMinutes) {
        String subject = "[" + mailProperties.getFromName() + "] " + purpose.getLabel() + " 인증 코드";
        String html = mailHtmlTemplateRenderer.renderVerification(purpose, code, expireMinutes);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailProperties.getFromAddress(), mailProperties.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            // layout.html의 cid:main-icon과 이름이 같아야 하고, setText 뒤에 호출해야 한다
            helper.addInline("main-icon", new ClassPathResource("mail/main-icon.png"));
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("인증 메일 발송 실패: to={}", to, e);
            throw new BusinessException(GlobalExceptionCode.MAIL_SEND_FAILED);
        }
    }
}
