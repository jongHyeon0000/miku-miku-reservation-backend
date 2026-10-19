package com.jong.mikumikureservationbackend.controller.auth;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.service.auth.EmailVerificationService;
import com.jong.mikumikureservationbackend.vo.user.request.EmailVerificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이메일 인증", description = "가입, 아이디 찾기, 비밀번호 재설정이 함께 쓰는 코드 확인. 코드 발송은 각 기능의 발송 엔드포인트(예: 가입은 POST /api/auth/signup/email-code)에서 하고, 여기서는 받은 코드가 맞는지 확인한다. 토큰 없이 호출한다.")
@RestController
@RequestMapping("/api/auth/email-verification")
@RequiredArgsConstructor
@SecurityRequirements
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "인증 코드 확인 (예: 메일로 받은 6자리 코드 입력)",
            description = "요청 본문: email: 이메일 (본문, 필수), purpose: 발송한 기능의 목적 SIGNUP 가입 / FIND_ID 아이디 찾기 / RESET_PASSWORD 비밀번호 재설정 (본문, 필수), code: 메일로 받은 6자리 코드 (본문, 필수)\n\n"
                    + "규칙: 코드가 맞으면 인증 완료가 되고 30분 안에 해당 기능(가입 등)에서 쓸 수 있다(쓰면 소모된다). 틀리면 횟수가 쌓이고 5회가 넘으면 코드를 다시 받아야 한다. 코드 유효 시간은 10분이다.\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "가입 코드 확인 예", value = "{\"email\":\"jong@example.com\",\"purpose\":\"SIGNUP\",\"code\":\"123456\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 코드를 받지 않았거나 만료, 코드 불일치(남은 횟수 안내), 확인 횟수 초과")
    })
    @PostMapping("/verify")
    public ApiResponse<Void> verify(@Valid @RequestBody EmailVerificationVO request) {
        emailVerificationService.verify(request.getEmail(), request.getPurpose(), request.getCode());
        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }
}
