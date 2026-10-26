package com.jong.mikumikureservationbackend.controller.auth.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.user.UserVO;
import com.jong.mikumikureservationbackend.vo.user.request.ChangePasswordRequestVO;
import com.jong.mikumikureservationbackend.vo.user.request.EmailVerificationVO;
import com.jong.mikumikureservationbackend.vo.user.request.LoginRequestVO;
import com.jong.mikumikureservationbackend.vo.user.request.ResetPasswordRequestVO;
import com.jong.mikumikureservationbackend.vo.user.request.WithdrawRequestVO;
import com.jong.mikumikureservationbackend.vo.user.response.FindIdResponseVO;
import com.jong.mikumikureservationbackend.vo.user.response.LoginResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/* AuthController의 Swagger 설명 전용 인터페이스. 로직은 AuthController에만 둔다 */
@Tag(name = "인증", description = "가입, 로그인(JWT 발급), 내 정보, 탈퇴. 로그인으로 받은 토큰을 Authorize에 넣으면 나머지 API를 호출할 수 있다.")
public interface AuthControllerDocs {

    @Operation(
            summary = "가입용 이메일 인증 코드 발송 (예: 가입할 이메일로 6자리 코드 받기)",
            description = "인증이 필요 없다(토큰 없이 호출). 가입 순서: 이 API로 코드 받기 → POST /api/auth/email-verification/verify로 코드 확인 → POST /api/auth/signup으로 가입\n\n"
                    + "요청 본문: email: 이메일 (본문, 필수, 100자 이하)\n\n"
                    + "규칙: 아직 가입되지 않은 이메일만 발송한다. 코드는 6자리 숫자이고 10분 동안 유효하며, 같은 이메일은 60초 뒤에 다시 받을 수 있다.\n\n"
                    + "응답: 성공 시 data 없음")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "발송 예", value = "{\"email\":\"user@example.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 이미 가입된 이메일, 60초 안에 재요청")
    })
    ApiResponse<Void> sendSignupEmailCode(EmailVerificationVO request);

    @Operation(
            summary = "회원가입 (예: 아이디 user1234, 닉네임 닉네임예시)",
            description = "인증이 필요 없다(토큰 없이 호출). 가입 전에 이메일 인증(코드 발송과 확인)을 끝내야 한다.\n\n"
                    + "요청 본문: loginId: 로그인 아이디 (본문, 필수, 영문과 숫자 4~30자), email: 이메일 (본문, 필수, 100자 이하), name: 닉네임 (본문, 필수, 20자 이하), password: 비밀번호 (본문, 필수, 8~20자)\n\n"
                    + "응답: 가입한 userId, loginId, email, name, createdAt, updatedAt (비밀번호 관련 값 없음)")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "가입 예",
                    value = "{\"loginId\":\"user1234\",\"email\":\"user@example.com\",\"name\":\"닉네임예시\",\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입한 userId, loginId, email, name, createdAt, updatedAt (비밀번호 관련 값 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아이디 또는 이메일 중복, 입력값 오류, 이메일 인증 미완료")
    })
    ApiResponse<UserVO> signup(UserVO request);

    @Operation(
            summary = "아이디 찾기용 이메일 인증 코드 발송 (예: 가입한 이메일로 6자리 코드 받기)",
            description = "인증이 필요 없다(토큰 없이 호출). 아이디 찾기 순서: 이 API로 코드 받기 → POST /api/auth/email-verification/verify(purpose: FIND_ID)로 코드 확인 → POST /api/auth/find-id로 아이디 받기\n\n"
                    + "요청 본문: email: 이메일 (본문, 필수, 100자 이하)\n\n"
                    + "규칙: 가입된 이메일만 발송한다. 코드는 6자리 숫자이고 10분 동안 유효하며, 같은 이메일은 60초 뒤에 다시 받을 수 있다.\n\n"
                    + "응답: 성공 시 data 없음")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "발송 예", value = "{\"email\":\"user@example.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 가입되지 않은 이메일, 60초 안에 재요청")
    })
    ApiResponse<Void> sendFindIdEmailCode(EmailVerificationVO request);

    @Operation(
            summary = "아이디 찾기 (예: 인증을 마친 이메일로 가입한 아이디 확인)",
            description = "인증이 필요 없다(토큰 없이 호출). 먼저 이메일 인증(코드 발송과 확인, purpose: FIND_ID)을 끝내야 한다.\n\n"
                    + "요청 본문: email: 이메일 (본문, 필수, 100자 이하)\n\n"
                    + "응답: loginId: 그 이메일로 가입한 로그인 아이디 (인증은 한 번 쓰면 소모된다)")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "아이디 찾기 예", value = "{\"email\":\"user@example.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "loginId: 그 이메일로 가입한 로그인 아이디"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 이메일 인증 미완료, 가입되지 않은 이메일")
    })
    ApiResponse<FindIdResponseVO> findId(EmailVerificationVO request);

    @Operation(
            summary = "비밀번호 재설정용 이메일 인증 코드 발송 (예: 아이디 user1234 의 가입 이메일로 6자리 코드 받기)",
            description = "인증이 필요 없다(토큰 없이 호출). 재설정 순서: 이 API로 코드 받기 → POST /api/auth/email-verification/verify(purpose: RESET_PASSWORD)로 코드 확인 → POST /api/auth/reset-password로 새 비밀번호 설정\n\n"
                    + "요청 본문: email: 이메일 (본문, 필수, 100자 이하), loginId: 로그인 아이디 (본문, 필수)\n\n"
                    + "규칙: 아이디와 이메일이 같은 계정일 때만 발송한다. 코드는 6자리 숫자이고 10분 동안 유효하며, 같은 이메일은 60초 뒤에 다시 받을 수 있다.\n\n"
                    + "응답: 성공 시 data 없음")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "발송 예", value = "{\"email\":\"user@example.com\",\"loginId\":\"user1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 아이디와 이메일이 같은 계정이 아님, 60초 안에 재요청")
    })
    ApiResponse<Void> sendResetPasswordEmailCode(EmailVerificationVO request);

    @Operation(
            summary = "비밀번호 재설정 (예: 이메일 인증을 마친 계정의 새 비밀번호 설정)",
            description = "인증이 필요 없다(토큰 없이 호출). 먼저 이메일 인증(코드 발송과 확인, purpose: RESET_PASSWORD)을 끝내야 하고, 확인 후 30분 안에 호출해야 한다.\n\n"
                    + "요청 본문: loginId: 로그인 아이디 (본문, 필수), email: 인증을 마친 이메일 (본문, 필수, 100자 이하), newPassword: 새 비밀번호 (본문, 필수, 8~20자)\n\n"
                    + "규칙: 아이디와 이메일이 같은 계정이어야 한다. 인증은 한 번 쓰면 소모되고, 30분이 지나면 코드 발송부터 다시 해야 한다.\n\n"
                    + "응답: 성공 시 data 없음")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "재설정 예",
                    value = "{\"loginId\":\"user1234\",\"email\":\"user@example.com\",\"newPassword\":\"newPassword1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류, 아이디와 이메일이 같은 계정이 아님, 이메일 인증 미완료 또는 30분 경과")
    })
    ApiResponse<Void> resetPassword(ResetPasswordRequestVO request);

    @Operation(
            summary = "로그인, JWT 발급 (예: user1234 로 로그인)",
            description = "인증이 필요 없다(토큰 없이 호출).\n\n"
                    + "요청 본문: loginId: 로그인 아이디 (본문, 필수), password: 비밀번호 (본문, 필수)\n\n"
                    + "응답: accessToken: 이후 요청의 Authorization: Bearer 헤더에 쓸 토큰 (24시간 유효)")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "로그인 예",
                    value = "{\"loginId\":\"user1234\",\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "accessToken: 이후 요청의 Authorization: Bearer 헤더에 쓸 토큰 (24시간 유효)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치 (둘을 구분하지 않음)")
    })
    ApiResponse<LoginResponseVO> login(LoginRequestVO request);

    @Operation(
            summary = "내 정보 조회 (예: 로그인한 계정의 닉네임)",
            description = "응답: userId, loginId, email, name, thumbnailUrl(프로필 썸네일, 없으면 생략), createdAt, updatedAt")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "userId, loginId, email, name, thumbnailUrl(없으면 생략), createdAt, updatedAt"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료, 변조, 탈퇴한 계정")
    })
    ApiResponse<UserVO> getMe();

    @Operation(
            summary = "비밀번호 변경 (예: 현재 비밀번호 확인 후 새 비밀번호로 변경)",
            description = "요청 본문: currentPassword: 현재 비밀번호 (본문, 필수), newPassword: 새 비밀번호 (본문, 필수, 8~20자)\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "변경 예",
                    value = "{\"currentPassword\":\"password1234\",\"newPassword\":\"newPassword1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료, 변조")
    })
    ApiResponse<Void> changePassword(ChangePasswordRequestVO request);

    @Operation(
            summary = "회원 탈퇴 (예: 내 계정을 탈퇴 상태로 변경)",
            description = "요청 본문: password: 현재 비밀번호 (본문, 필수)\n\n"
                    + "규칙: 계정을 지우지 않고 탈퇴 상태로 바꾼다. 내가 쓴 글과 댓글도 탈퇴 상태가 되어 내용이 보이지 않는다. 상품, 분류, 이미지는 그대로 남는다. 탈퇴하면 기존 토큰은 401이 되고, 같은 아이디, 이메일로 다시 가입할 수 있으며 가입하면 새 계정으로 시작한다(이전 데이터는 이어지지 않는다).\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "탈퇴 예",
                    value = "{\"password\":\"password1234\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 불일치, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료, 변조")
    })
    ApiResponse<Void> withdraw(WithdrawRequestVO request);
}
