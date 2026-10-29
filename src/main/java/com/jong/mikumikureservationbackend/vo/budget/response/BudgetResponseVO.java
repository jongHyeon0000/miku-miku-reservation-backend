package com.jong.mikumikureservationbackend.vo.budget.response;

import com.jong.mikumikureservationbackend.vo.budget.BudgetAdjustVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/* 예산 현황. 꺼져 있으면 period는 null이고 목록은 비어 있다 */
@Schema(description = "예산 현황. 꺼져 있으면 period는 null이고 adjusts, spendings는 빈 배열")
@Getter
@Setter
@NoArgsConstructor
public class BudgetResponseVO {

    /** [response] 예산 사용 여부 */
    @Schema(description = "예산 사용 여부", example = "true")
    private Boolean enabled;

    /** [response] 바로 다시 켤 수 있는지 (꺼져 있을 때만 의미, 켜져 있으면 항상 false) */
    @Schema(description = "꺼져 있을 때만 의미. 이번 기간 기록이 남아 있어 입력 없이 다시 켤 수 있는지. 켜져 있으면 항상 false", example = "false")
    private Boolean resumable;

    /** [response] 기간 시작일 설정 */
    @Schema(description = "기간 시작일 설정(1~28). 꺼져 있으면 마지막 설정값(처음이면 1)", example = "25")
    private Integer startDay;

    /** [response] 기본 예산 설정 */
    @Schema(description = "기본 예산 설정(원). 꺼져 있으면 마지막 설정값(처음이면 0)", example = "550000")
    private Integer defaultBudget;

    /** [response] 이번 기간 현황 */
    @Schema(description = "이번 기간 현황. 꺼져 있으면 null", nullable = true)
    private BudgetPeriodResponseVO period;

    /** [response] 이번 기간 증감 목록 (최근 순) */
    @Schema(description = "이번 기간 증감 목록(최근 순)")
    private List<BudgetAdjustVO> adjusts;

    /** [response] 이번 기간 지출 목록 (최근 결제일 순) */
    @Schema(description = "이번 기간 지출 목록(최근 결제일 순)")
    private List<BudgetSpendingVO> spendings;
}
