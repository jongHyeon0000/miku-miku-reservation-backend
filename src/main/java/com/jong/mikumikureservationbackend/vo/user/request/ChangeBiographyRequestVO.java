package com.jong.mikumikureservationbackend.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 자기소개 변경 요청 (DB 테이블 없음)
 * 로그인한 상태에서 새 자기소개를 보낸다. 비우려면 빈 문자열이나 null을 보낸다
 */
@Schema(description = "자기소개 변경 요청. 로그인한 상태에서 새 자기소개를 보낸다. 비우려면 빈 문자열이나 null")
@Getter
@Setter
@NoArgsConstructor
public class ChangeBiographyRequestVO {

    /** [request] 새 자기소개 (300자 이하, 비어 있어도 된다) */
    @Schema(description = "새 자기소개 (앞뒤 공백을 뺀 300자 이하, 비어 있으면 자기소개를 지운다)", example = "안녕하세요! 미쿠 덕후입니다 💙", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 300)
    private String biography;
}
