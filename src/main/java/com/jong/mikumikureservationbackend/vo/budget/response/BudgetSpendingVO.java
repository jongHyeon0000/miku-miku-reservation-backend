package com.jong.mikumikureservationbackend.vo.budget.response;

import com.jong.mikumikureservationbackend.common.constant.BudgetSpendingKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/* 지출 한 줄 (상품의 결제 한 번). 한 상품이 예약금과 잔금을 모두 같은 기간에 냈으면 두 줄이다 */
@Schema(description = "지출 한 줄 (상품의 결제 한 번)")
@Getter
@Setter
@NoArgsConstructor
public class BudgetSpendingVO {

    /** [response] 상품 id */
    @Schema(description = "상품 id", example = "31")
    private Long figureId;

    /** [response] 상품명 */
    @Schema(description = "상품명", example = "하츠네 미쿠 1/7 스케일")
    private String figureName;

    /** [response] 결제 구분 */
    @Schema(description = "결제 구분. FULL 전액, DEPOSIT 예약금, BALANCE 잔금", example = "FULL")
    private BudgetSpendingKind kind;

    /** [response] 결제일 */
    @Schema(description = "결제일", example = "2026-11-02")
    private LocalDate paidDate;

    /** [response] 그 결제로 나간 금액 */
    @Schema(description = "그 결제로 나간 금액(원)", example = "220000")
    private Integer amount;
}
