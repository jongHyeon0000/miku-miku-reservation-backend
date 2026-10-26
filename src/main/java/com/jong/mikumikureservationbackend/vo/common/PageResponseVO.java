package com.jong.mikumikureservationbackend.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 페이지네이션 목록 응답 (분류 하위, 제조사, 커뮤니티 게시글). 무한스크롤 목록과는 별개다.
 */
@Schema(description = "페이지네이션 목록 응답. items 안의 항목 모양은 엔드포인트마다 다르다")
@Getter
@Setter
@NoArgsConstructor
public class PageResponseVO<T> {

    @Schema(description = "이번 페이지 항목. 없으면 빈 배열")
    private List<T> items;

    @Schema(description = "적용된 페이지 번호 (1부터)", example = "1")
    private int page;

    @Schema(description = "적용된 한 페이지 개수 (기본 10)", example = "10")
    private int size;

    @Schema(description = "조건에 맞는 전체 항목 수", example = "297")
    private long totalCount;

    @Schema(description = "전체 페이지 수. 항목이 없으면 0", example = "30")
    private int totalPages;

    public PageResponseVO(List<T> items, int page, int size, long totalCount) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = (int) ((totalCount + size - 1) / size);
    }
}
