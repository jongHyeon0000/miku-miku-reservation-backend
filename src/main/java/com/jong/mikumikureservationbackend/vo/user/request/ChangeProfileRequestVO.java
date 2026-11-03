package com.jong.mikumikureservationbackend.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 프로필 변경 요청 (DB 테이블 없음)
 * 로그인한 상태에서 닉네임과 자기소개를 함께 보낸다 (보낸 값으로 통째로 교체한다)
 */
@Schema(description = "프로필 변경 요청. 로그인한 상태에서 닉네임과 자기소개를 함께 보낸다. 보낸 값으로 통째로 교체하므로 자기소개를 빼고 보내면 자기소개가 지워진다")
@Getter
@Setter
@NoArgsConstructor
public class ChangeProfileRequestVO {

    /** [request] 새 닉네임 (한글 포함 20자 이하) */
    @Schema(description = "새 닉네임 (앞뒤 공백을 뺀 1~20자, 다른 사용자와 같아도 된다)", example = "새닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 20)
    private String name;

    /** [request] 새 자기소개 (300자 이하, 비어 있어도 된다) */
    @Schema(description = "새 자기소개 (앞뒤 공백을 뺀 300자 이하, 이모지도 1자로 센다, 비어 있거나 null이면 자기소개를 지운다)", example = "안녕하세요! 미쿠 덕후입니다 💙", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String biography;
}
