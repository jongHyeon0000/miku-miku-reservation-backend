package com.jong.mikumikureservationbackend.vo.figure.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 상품 일괄 삭제 요청 본문.
 * [request] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "상품 일괄 삭제 요청 본문")
@Getter
@Setter
@NoArgsConstructor
public class FigureDeleteRequestVO {

    /** [request] 삭제할 상품 id 목록 */
    @Schema(description = "삭제할 상품 id 목록. 1개 이상", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> figureIds;
}
