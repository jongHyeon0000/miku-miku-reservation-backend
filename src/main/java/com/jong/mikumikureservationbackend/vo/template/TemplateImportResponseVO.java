package com.jong.mikumikureservationbackend.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "가져오기 결과")
@Getter
@RequiredArgsConstructor
public class TemplateImportResponseVO {

    @Schema(description = "새로 추가된 하위 항목 수", example = "297")
    private final int addedCount;

    @Schema(description = "이미 있어서 건너뛴 항목 수", example = "3")
    private final int skippedCount;
}
