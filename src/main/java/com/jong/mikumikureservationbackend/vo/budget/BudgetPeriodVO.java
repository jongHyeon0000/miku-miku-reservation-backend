package com.jong.mikumikureservationbackend.vo.budget;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예산 기간 (테이블 budget_period)
 * 테이블과 1:1인 순수 VO. 한 기간의 기본 예산이며 만들어진 뒤에는 수정하지 않는다.
 */
@Schema(description = "예산 기간 (테이블 budget_period과 1:1)")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetPeriodVO {

    /** PK */
    @Schema(description = "기간 id", example = "12")
    private Long budgetPeriodId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id (응답에서는 보통 생략)", example = "1")
    private Long userId;

    /** 기간 시작일 */
    @Schema(description = "기간 시작일", example = "2026-10-25")
    private LocalDate startDate;

    /** 기간 종료일 (그날 포함) */
    @Schema(description = "기간 종료일 (그날 포함)", example = "2026-11-24")
    private LocalDate endDate;

    /** 그 기간의 기본 예산(원), 만들어진 뒤 수정하지 않음 */
    @Schema(description = "그 기간의 기본 예산(원)", example = "550000")
    private Integer amount;

    /** 생성일 */
    @Schema(description = "생성일")
    private LocalDateTime createdAt;
}
