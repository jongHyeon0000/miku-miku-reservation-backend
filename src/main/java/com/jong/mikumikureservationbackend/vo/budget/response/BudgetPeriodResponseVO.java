package com.jong.mikumikureservationbackend.vo.budget.response;

import com.jong.mikumikureservationbackend.vo.budget.BudgetPeriodVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 이번 기간 현황. 기간 한 줄에 증감 합계와 지출로 계산한 값이 더해진다 */
@Schema(description = "이번 기간 현황")
@Getter
@Setter
@NoArgsConstructor
public class BudgetPeriodResponseVO extends BudgetPeriodVO {

    /** [response] 증감 합계 */
    @Schema(description = "증감 합계(원, 부호 있음)", example = "70000")
    private Integer adjustTotal;

    /** [response] 총 예산 = 기본 예산 + 증감 합계 */
    @Schema(description = "총 예산(원) = 기본 예산 + 증감 합계", example = "620000")
    private Integer totalBudget;

    /** [response] 지출 합계 */
    @Schema(description = "지출 합계(원). 상품의 결제일이 이 기간에 속하는 결제 금액의 합", example = "400000")
    private Integer spent;

    /** [response] 남은 돈 = 총 예산 - 지출 (음수 가능) */
    @Schema(description = "남은 돈(원) = 총 예산 - 지출. 음수일 수 있다", example = "220000")
    private Integer remaining;
}
