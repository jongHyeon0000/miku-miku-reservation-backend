package com.jong.mikumikureservationbackend.service.auth.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.common.security.AuthUser;
import com.jong.mikumikureservationbackend.common.security.JwtProvider;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterMapper;
import com.jong.mikumikureservationbackend.mapper.maker.MakerMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesGroupMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesMapper;
import com.jong.mikumikureservationbackend.mapper.shop.ShopGroupMapper;
import com.jong.mikumikureservationbackend.mapper.shop.ShopMapper;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeMapper;
import com.jong.mikumikureservationbackend.mapper.user.UserMapper;
import com.jong.mikumikureservationbackend.service.auth.AuthService;
import com.jong.mikumikureservationbackend.service.auth.EmailVerificationService;
import com.jong.mikumikureservationbackend.vo.user.response.FindIdResponseVO;
import com.jong.mikumikureservationbackend.vo.user.request.LoginRequestVO;
import com.jong.mikumikureservationbackend.vo.user.response.LoginResponseVO;
import com.jong.mikumikureservationbackend.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final FigureTypeMapper figureTypeMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final MakerMapper makerMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public UserVO signup(UserVO request) {
        String loginId = request.getLoginId();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String name = request.getName().trim();

        if (name.isEmpty() || name.length() > 20) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "name: 앞뒤 공백을 뺀 1~20자여야 합니다");
        }

        if (userMapper.selectByLoginId(loginId) != null) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 아이디입니다");
        }
        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 이메일입니다");
        }

        emailVerificationService.consume(email, EmailVerificationPurpose.SIGNUP);

        try {
            userMapper.insert(loginId, email, name, passwordEncoder.encode(request.getPassword()));
        } catch (DuplicateKeyException e) {
            // 중복 확인 직후 동시에 가입한 경우는 UNIQUE 제약이 막는다
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 아이디 또는 이메일입니다");
        }

        Long userId = userMapper.selectLastInsertId();

        createDefaultCategories(userId);

        return userMapper.selectById(userId);
    }

    @Override
    public void sendSignupEmailCode(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);

        if (userMapper.selectByEmail(normalized) != null) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 이메일입니다");
        }

        emailVerificationService.sendCode(normalized, EmailVerificationPurpose.SIGNUP);
    }

    @Override
    public void sendFindIdEmailCode(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);

        if (userMapper.selectByEmail(normalized) == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "가입된 이메일이 아닙니다");
        }

        emailVerificationService.sendCode(normalized, EmailVerificationPurpose.FIND_ID);
    }

    @Override
    public FindIdResponseVO findId(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);

        emailVerificationService.consume(normalized, EmailVerificationPurpose.FIND_ID);

        UserVO user = userMapper.selectByEmail(normalized);

        if (user == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "가입된 이메일이 아닙니다");
        }

        return new FindIdResponseVO(user.getLoginId());
    }

    @Override
    public void sendResetPasswordEmailCode(String loginId, String email) {
        if (loginId == null || loginId.trim().isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "loginId: 아이디를 입력해 주세요");
        }

        String normalized = email.trim().toLowerCase(Locale.ROOT);

        selectMatchingUser(loginId, normalized);
        emailVerificationService.sendCode(normalized, EmailVerificationPurpose.RESET_PASSWORD);
    }

    @Override
    @Transactional
    public void resetPassword(String loginId, String email, String newPassword) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        // 인증 표시는 이메일 기준이라 아이디와 이메일이 같은 계정인지 먼저 확인하고, 통과한 뒤에만 소모한다
        UserVO user = selectMatchingUser(loginId, normalized);

        emailVerificationService.consume(normalized, EmailVerificationPurpose.RESET_PASSWORD);
        updatePassword(user.getUserId(), newPassword);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        UserVO user = getMe(userId);

        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(user.getLoginId(), currentPassword));
        } catch (AuthenticationException e) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다");
        }

        updatePassword(userId, newPassword);
    }

    @Override
    public LoginResponseVO login(LoginRequestVO request) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.getLoginId(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BusinessException(GlobalExceptionCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다");
        }

        AuthUser principal = (AuthUser) authentication.getPrincipal();

        return new LoginResponseVO(jwtProvider.createToken(principal.getUserId()));
    }

    @Override
    public UserVO getMe(Long userId) {
        UserVO user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return user;
    }

    @Override
    @Transactional
    public void withdraw(Long userId, String password) {
        UserVO user = getMe(userId);

        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(user.getLoginId(), password));
        } catch (AuthenticationException e) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "비밀번호가 일치하지 않습니다");
        }

        userMapper.withdraw(userId);
    }

    private UserVO selectMatchingUser(String loginId, String normalizedEmail) {
        UserVO user = userMapper.selectByLoginId(loginId.trim());

        if (user == null || !normalizedEmail.equals(user.getEmail())) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "아이디와 이메일이 일치하는 계정이 없습니다");
        }

        return user;
    }

    private void updatePassword(Long userId, String newPassword) {
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    private void createDefaultCategories(Long userId) {
        String name = CategoryConstant.DEFAULT_NAME;

        shopGroupMapper.insert(userId, name, null);
        shopMapper.insert(userId, shopGroupMapper.selectByName(userId, name).getId(), name, null, null);

        figureTypeGroupMapper.insert(userId, name, null);
        figureTypeMapper.insert(userId, figureTypeGroupMapper.selectByName(userId, name).getId(), name, null);

        seriesGroupMapper.insert(userId, name, null);
        seriesMapper.insert(userId, seriesGroupMapper.selectByName(userId, name).getId(), name, null);

        genreCharacterGroupMapper.insert(userId, name, null);
        genreCharacterMapper.insert(userId,
                genreCharacterGroupMapper.selectByName(userId, name).getId(), name, null);

        makerMapper.insert(userId, name, null);
    }
}
