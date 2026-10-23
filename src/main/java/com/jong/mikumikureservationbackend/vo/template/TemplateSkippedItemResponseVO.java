package com.jong.mikumikureservationbackend.vo.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "제외될 항목 한 건. 템플릿에 있는 값(설명, 이미지)이며 내가 이미 가진 항목의 값이 아니다")
@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateSkippedItemResponseVO {

    @Schema(description = "항목 이름", example = "하쿠레이 레이무")
    private final String name;

    @Schema(description = "설명 (없으면 생략)", example = "낙원의 무녀")
    private final String description;

    @Schema(description = "이미지 주소 (없으면 생략)", example = "/uploads/template/thumbnail-image/character/reimu.png")
    private final String thumbnailUrl;
}
