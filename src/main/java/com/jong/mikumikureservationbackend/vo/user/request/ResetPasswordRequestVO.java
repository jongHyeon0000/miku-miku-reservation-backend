package com.jong.mikumikureservationbackend.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정 요청 (DB 테이블 없음)
 * 이메일 인증을 마친 뒤 아이디, 이메일, 새 비밀번호를 보낸다
 */
@Schema(description = "비밀번호 재설정 요청. 이메일 인증을 마친 뒤 아이디, 이메일, 새 비밀번호를 보낸다")
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequestVO {

    /** [request] 로그인 아이디 (이메일과 같은 계정이어야 함) */
    @Schema(description = "로그인 아이디 (이메일과 같은 계정이어야 함)", example = "jong1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String loginId;

    /** [request] 이메일 (인증을 마친 이메일, 소문자로 바꿔 처리) */
    @Schema(description = "이메일 (인증을 마친 이메일, 소문자로 바꿔 처리)", example = "jong@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    /** [request] 새 비밀번호 (평문, 8~20자) */
    @Schema(description = "새 비밀번호 (평문, 8~20자)", example = "newPassword1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;
}
