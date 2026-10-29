package com.jong.mikumikureservationbackend.vo.budget.request;

import com.jong.mikumikureservationbackend.common.constant.FigureStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/* 결제 전 예산 확인 요청 본문. 상태 전이 요청에서 계산에 필요한 값 5개만 받는다 */
@Schema(description = "결제 전 예산 확인 요청 본문. 상태 전이 요청에서 계산에 필요한 값만 골라 보낸다")
@Getter
@Setter
@NoArgsConstructor
public class BudgetPaymentCheckRequestVO {

    /** [request] 대상 상품 id 목록 */
    @Schema(description = "결제로 넘길 상품 id 목록 (단건도 1개짜리 목록, 예약완료는 1개만)", example = "[31, 32]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<Long> figureIds;

    /** [request] 다음 상태 */
    @Schema(description = "다음 상태. RESERVED(예약완료) 또는 PURCHASED(구매완료)", example = "PURCHASED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private FigureStatus status;

    /** [request] 예약금 */
    @Schema(description = "예약금(원). RESERVED일 때 필수", example = "20000")
    @Min(0)
    private Integer depositAmount;

    /** [request] 예약금 결제일 */
    @Schema(description = "예약금 결제일. 생략하면 서버의 오늘 날짜", example = "2026-10-30")
    private LocalDate depositPaidAt;

    /** [request] 전액 또는 잔금 결제일 */
    @Schema(description = "전액 또는 잔금 결제일. 생략하면 서버의 오늘 날짜", example = "2026-10-28")
    private LocalDate paidAt;
}
