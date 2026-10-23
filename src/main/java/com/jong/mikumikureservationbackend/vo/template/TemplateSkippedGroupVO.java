package com.jong.mikumikureservationbackend.vo.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema(description = "제외될 항목을 상위 그룹별로 묶은 것")
@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateSkippedGroupVO {

    @Schema(description = "상위 그룹 이름. 제조사 템플릿은 상위가 없어 생략된다", example = "동방프로젝트")
    private final String groupName;

    @Schema(description = "제외될 항목들 (이름, 설명, 이미지)")
    private final List<TemplateSkippedItemResponseVO> items;
}
