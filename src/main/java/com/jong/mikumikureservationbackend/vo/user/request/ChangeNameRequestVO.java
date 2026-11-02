package com.jong.mikumikureservationbackend.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 닉네임 변경 요청 (DB 테이블 없음)
 * 로그인한 상태에서 새 닉네임을 보낸다
 */
@Schema(description = "닉네임 변경 요청. 로그인한 상태에서 새 닉네임을 보낸다")
@Getter
@Setter
@NoArgsConstructor
public class ChangeNameRequestVO {

    /** [request] 새 닉네임 (한글 포함 20자 이하) */
    @Schema(description = "새 닉네임 (앞뒤 공백을 뺀 1~20자, 다른 사용자와 같아도 된다)", example = "새닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 20)
    private String name;
}
