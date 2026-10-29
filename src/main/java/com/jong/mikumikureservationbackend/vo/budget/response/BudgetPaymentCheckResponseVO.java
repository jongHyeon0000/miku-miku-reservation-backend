package com.jong.mikumikureservationbackend.vo.budget.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 결제 전 예산 확인 결과. 예산이 꺼져 있거나 결제일이 속한 기간이 없으면 exceeded는 false이고 금액은 null이다 */
@Schema(description = "결제 전 예산 확인 결과. 예산이 꺼져 있거나 결제일이 속한 기간이 없으면 exceeded는 false")
@Getter
@Setter
@NoArgsConstructor
public class BudgetPaymentCheckResponseVO {

    /** [response] 결제하면 예산을 넘는지 */
    @Schema(description = "결제하면 예산을 넘는지", example = "true")
    private Boolean exceeded;

    /** [response] 이번에 나갈 결제 금액 합계 */
    @Schema(description = "이번에 나갈 결제 금액 합계(원). 예산이 꺼져 있으면 null", example = "200000", nullable = true)
    private Integer payAmount;

    /** [response] 결제일이 속한 기간의 현재 남은 돈 */
    @Schema(description = "결제일이 속한 기간의 현재 남은 돈(원). 예산이 꺼져 있거나 그 기간이 없으면 null", example = "70000", nullable = true)
    private Integer remaining;

    /** [response] 결제 후 남은 돈 */
    @Schema(description = "결제 후 남은 돈(원) = remaining - payAmount. 초과면 음수", example = "-130000", nullable = true)
    private Integer remainingAfter;
}
