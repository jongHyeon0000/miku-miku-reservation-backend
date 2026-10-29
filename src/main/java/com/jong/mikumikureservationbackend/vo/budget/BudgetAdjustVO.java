package com.jong.mikumikureservationbackend.vo.budget;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 예산 증감 (테이블 budget_adjust)
 * 테이블과 1:1인 순수 VO. 기간별로 예산을 더하거나(+) 빼는(-) 내역 한 줄이다.
 */
@Schema(description = "예산 증감 (테이블 budget_adjust와 1:1)")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetAdjustVO {

    /** PK */
    @Schema(description = "증감 id", example = "5")
    private Long budgetAdjustId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id (응답에서는 생략)", example = "1")
    private Long userId;

    /** 속한 기간 */
    @Schema(description = "속한 기간 id", example = "12")
    private Long budgetPeriodId;

    /** 증감 금액(원), 더하면 양수, 빼면 음수 */
    @Schema(description = "증감 금액(원). 더하면 양수, 빼면 음수", example = "-30000")
    private Integer amount;

    /** 이유 메모 (보너스, 경조사 등) */
    @Schema(description = "이유 메모", example = "경조사", nullable = true)
    private String memo;

    /** 생성일 */
    @Schema(description = "등록 시각")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정 시각")
    private LocalDateTime updatedAt;
}
