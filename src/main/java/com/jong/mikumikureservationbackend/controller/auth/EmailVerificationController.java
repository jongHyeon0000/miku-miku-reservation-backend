package com.jong.mikumikureservationbackend.controller.auth;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.controller.auth.doc.EmailVerificationControllerDocs;
import com.jong.mikumikureservationbackend.service.auth.EmailVerificationService;
import com.jong.mikumikureservationbackend.vo.user.request.EmailVerificationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController implements EmailVerificationControllerDocs {

    private final EmailVerificationService emailVerificationService;

    @Override
    @PostMapping("/verify")
    public ApiResponse<Void> verify(@Valid @RequestBody EmailVerificationVO request) {
        emailVerificationService.verify(request.getEmail(), request.getPurpose(), request.getCode());

        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }
}
