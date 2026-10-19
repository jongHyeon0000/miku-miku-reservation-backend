package com.jong.mikumikureservationbackend.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 변경 요청 (DB 테이블 없음)
 * 로그인한 상태에서 현재 비밀번호와 새 비밀번호를 보낸다
 */
@Schema(description = "비밀번호 변경 요청. 로그인한 상태에서 현재 비밀번호와 새 비밀번호를 보낸다")
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestVO {

    /** [request] 현재 비밀번호 (평문) */
    @Schema(description = "현재 비밀번호 (평문)", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String currentPassword;

    /** [request] 새 비밀번호 (평문, 8~20자) */
    @Schema(description = "새 비밀번호 (평문, 8~20자)", example = "newPassword1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;
}
