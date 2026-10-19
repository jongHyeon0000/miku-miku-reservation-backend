package com.jong.mikumikureservationbackend.vo.user;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이메일 인증 요청 (DB 테이블 없음, 인증 정보는 Redis에 저장)
 * 가입 코드 발송 요청은 email만, 코드 확인 요청은 email, purpose, code를 읽는다.
 */
@Schema(description = "이메일 인증 요청. 가입 코드 발송은 email만, 코드 확인은 email, purpose, code를 읽는다")
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationVO {

    /** [request] 인증 대상 이메일 (소문자로 바꿔 처리) */
    @Schema(description = "인증 대상 이메일 (소문자로 바꿔 처리)", example = "jong@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    /** [request] 목적: SIGNUP 가입, FIND_ID 아이디 찾기, RESET_PASSWORD 비밀번호 재설정. 코드 확인에서만 필수 */
    @Schema(description = "목적: SIGNUP 가입, FIND_ID 아이디 찾기, RESET_PASSWORD 비밀번호 재설정. 코드 확인에서만 필수", example = "SIGNUP")
    private EmailVerificationPurpose purpose;

    /** [request] 이메일로 받은 6자리 인증 코드. 코드 확인에서만 필수 */
    @Schema(description = "이메일로 받은 6자리 인증 코드. 코드 확인에서만 필수", example = "123456")
    private String code;
}
