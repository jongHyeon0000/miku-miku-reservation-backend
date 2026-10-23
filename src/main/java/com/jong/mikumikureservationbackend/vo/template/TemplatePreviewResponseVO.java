package com.jong.mikumikureservationbackend.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema(description = "가져오기 미리보기: 가져오면 추가될 수와 이미 있어 제외될 항목")
@Getter
@RequiredArgsConstructor
public class TemplatePreviewResponseVO {

    @Schema(description = "템플릿 id", example = "1")
    private final Long templateId;

    @Schema(description = "템플릿 이름", example = "동방프로젝트")
    private final String name;

    @Schema(description = "가져오면 새로 추가될 하위 항목 수", example = "297")
    private final int addCount;

    @Schema(description = "이미 있어서 제외될 항목 수 (skipped의 모든 이름 합계)", example = "3")
    private final int skippedCount;

    @Schema(description = "제외될 항목. 제외 항목이 없으면 빈 배열")
    private final List<TemplateSkippedGroupVO> skipped;
}
