package com.jong.mikumikureservationbackend.controller.auth;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.auth.doc.AuthControllerDocs;
import com.jong.mikumikureservationbackend.service.auth.AuthService;
import com.jong.mikumikureservationbackend.vo.user.request.ChangePasswordRequestVO;
import com.jong.mikumikureservationbackend.vo.user.request.EmailVerificationVO;
import com.jong.mikumikureservationbackend.vo.user.response.FindIdResponseVO;
import com.jong.mikumikureservationbackend.vo.user.request.LoginRequestVO;
import com.jong.mikumikureservationbackend.vo.user.response.LoginResponseVO;
import com.jong.mikumikureservationbackend.vo.user.request.ResetPasswordRequestVO;
import com.jong.mikumikureservationbackend.vo.user.UserVO;
import com.jong.mikumikureservationbackend.vo.user.request.WithdrawRequestVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final CurrentUser currentUser;

    @Override
    @PostMapping("/signup/email-code")
    public ApiResponse<Void> sendSignupEmailCode(@Valid @RequestBody EmailVerificationVO request) {
        authService.sendSignupEmailCode(request.getEmail());

        return ApiResponse.ok("인증 코드를 메일로 보냈습니다.");
    }

    @Override
    @PostMapping("/signup")
    public ApiResponse<UserVO> signup(@Valid @RequestBody UserVO request) {
        return ApiResponse.ok("가입이 완료되었습니다.", authService.signup(request));
    }

    @Override
    @PostMapping("/find-id/email-code")
    public ApiResponse<Void> sendFindIdEmailCode(@Valid @RequestBody EmailVerificationVO request) {
        authService.sendFindIdEmailCode(request.getEmail());

        return ApiResponse.ok("인증 코드를 메일로 보냈습니다.");
    }

    @Override
    @PostMapping("/find-id")
    public ApiResponse<FindIdResponseVO> findId(@Valid @RequestBody EmailVerificationVO request) {
        return ApiResponse.ok("조회가 완료되었습니다.", authService.findId(request.getEmail()));
    }

    @Override
    @PostMapping("/reset-password/email-code")
    public ApiResponse<Void> sendResetPasswordEmailCode(@Valid @RequestBody EmailVerificationVO request) {
        authService.sendResetPasswordEmailCode(request.getLoginId(), request.getEmail());

        return ApiResponse.ok("인증 코드를 메일로 보냈습니다.");
    }

    @Override
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestVO request) {
        authService.resetPassword(request.getLoginId(), request.getEmail(), request.getNewPassword());

        return ApiResponse.ok("비밀번호가 변경되었습니다.");
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<LoginResponseVO> login(@Valid @RequestBody LoginRequestVO request) {
        return ApiResponse.ok("로그인되었습니다.", authService.login(request));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserVO> getMe() {
        return ApiResponse.ok("조회가 완료되었습니다.", authService.getMe(currentUser.getId()));
    }

    @Override
    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequestVO request) {
        authService.changePassword(currentUser.getId(), request.getCurrentPassword(), request.getNewPassword());

        return ApiResponse.ok("비밀번호가 변경되었습니다.");
    }

    @Override
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@Valid @RequestBody WithdrawRequestVO request) {
        authService.withdraw(currentUser.getId(), request.getPassword());

        return ApiResponse.ok("탈퇴가 완료되었습니다.");
    }
}
