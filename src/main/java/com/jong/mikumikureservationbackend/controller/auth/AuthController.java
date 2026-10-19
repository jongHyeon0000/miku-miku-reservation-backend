package com.jong.mikumikureservationbackend.controller.auth;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.service.auth.AuthService;
import com.jong.mikumikureservationbackend.vo.user.EmailVerificationVO;
import com.jong.mikumikureservationbackend.vo.user.LoginRequestVO;
import com.jong.mikumikureservationbackend.vo.user.LoginResponse;
import com.jong.mikumikureservationbackend.vo.user.UserVO;
import com.jong.mikumikureservationbackend.vo.user.WithdrawRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "가입, 로그인(JWT 발급), 내 정보, 탈퇴. 로그인으로 받은 토큰을 Authorize에 넣으면 나머지 API를 호출할 수 있다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "가입용 이메일 인증 코드 발송 (예: 가입할 이메일로 6자리 코드 받기)",
            description = "인증이 필요 없다(토큰 없이 호출). 가입 순서: 이 API로 코드 받기 → POST /api/auth/email-verification/verify로 코드 확인 → POST /api/auth/signup으로 가입\n\n"
                    + "요청 본문: email: 이메일 (본문, 필수, 100자 이하)\n\n"
                    + "규칙: 아직 가입되지 않은 이메일만 발송한다. 코드는 6자리 숫자이고 10분 동안 유효하며, 같은 이메일은 60초 뒤에 다시 받을 수 있다.\n\n"
                    + "응답: 성공 시 data 없음")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "발송 예", value = "{\"email\":\"jong@example.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 이미 가입된 이메일, 60초 안에 재요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "메일 발송 실패")
    })
    @PostMapping("/signup/email-code")
    public ApiResponse<Void> sendSignupEmailCode(@Valid @RequestBody EmailVerificationVO request) {
        authService.sendSignupEmailCode(request.getEmail());
        return ApiResponse.ok("인증 코드를 메일로 보냈습니다.");
    }

    @Operation(
            summary = "회원가입 (예: 아이디 jong1234, 닉네임 종현)",
            description = "인증이 필요 없다(토큰 없이 호출). 가입 전에 이메일 인증(코드 발송과 확인)을 끝내야 한다.\n\n"
                    + "요청 본문: loginId: 로그인 아이디 (본문, 필수, 영문과 숫자 4~30자), email: 이메일 (본문, 필수, 100자 이하), name: 닉네임 (본문, 필수, 20자 이하), password: 비밀번호 (본문, 필수, 8~20자)\n\n"
                    + "응답: 가입한 userId, loginId, email, name, createdAt, updatedAt (비밀번호 관련 값 없음)")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "가입 예",
                    value = "{\"loginId\":\"jong1234\",\"email\":\"jong@example.com\",\"name\":\"종현\",\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입한 userId, loginId, email, name, createdAt, updatedAt (비밀번호 관련 값 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아이디 또는 이메일 중복, 입력값 오류, 이메일 인증 미완료")
    })
    @PostMapping("/signup")
    public ApiResponse<UserVO> signup(@Valid @RequestBody UserVO request) {
        return ApiResponse.ok("가입이 완료되었습니다.", authService.signup(request));
    }

    @Operation(
            summary = "로그인, JWT 발급 (예: jong1234 로 로그인)",
            description = "인증이 필요 없다(토큰 없이 호출).\n\n"
                    + "요청 본문: loginId: 로그인 아이디 (본문, 필수), password: 비밀번호 (본문, 필수)\n\n"
                    + "응답: accessToken: 이후 요청의 Authorization: Bearer 헤더에 쓸 토큰 (24시간 유효)")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "로그인 예",
                    value = "{\"loginId\":\"jong1234\",\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "accessToken: 이후 요청의 Authorization: Bearer 헤더에 쓸 토큰 (24시간 유효)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치 (둘을 구분하지 않음)")
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequestVO request) {
        return ApiResponse.ok("로그인되었습니다.", authService.login(request));
    }

    @Operation(
            summary = "내 정보 조회 (예: 로그인한 계정의 닉네임)",
            description = "응답: userId, loginId, email, name, thumbnailUrl(프로필 썸네일, 없으면 생략), createdAt, updatedAt")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "userId, loginId, email, name, thumbnailUrl(없으면 생략), createdAt, updatedAt"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료, 변조, 탈퇴한 계정")
    })
    @GetMapping("/me")
    public ApiResponse<UserVO> getMe() {
        return ApiResponse.ok("조회가 완료되었습니다.", authService.getMe(currentUser.getId()));
    }

    @Operation(
            summary = "회원 탈퇴 (예: 내 계정, 상품, 분류, 업로드 이미지 전부 삭제)",
            description = "요청 본문: password: 현재 비밀번호 (본문, 필수)\n\n"
                    + "응답: 성공 시 data 없음 (되돌릴 수 없다)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "탈퇴 예",
                    value = "{\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음 (되돌릴 수 없다)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 불일치, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료, 변조")
    })
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@Valid @RequestBody WithdrawRequestVO request) {
        authService.withdraw(currentUser.getId(), request.getPassword());
        return ApiResponse.ok("탈퇴가 완료되었습니다.");
    }
}
