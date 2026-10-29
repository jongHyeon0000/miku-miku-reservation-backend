package com.jong.mikumikureservationbackend.vo.budget.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 예산 시작하기와 설정 변경 요청 본문. 두 값을 항상 같이 받는다 */
@Schema(description = "예산 설정 요청 본문 (시작하기, 설정 변경 공용). 두 값을 항상 같이 보낸다")
@Getter
@Setter
@NoArgsConstructor
public class BudgetSettingRequestVO {

    /** [request] 기간 시작일 */
    @Schema(description = "기간 시작일(1~28). 시작일부터 다음 달 시작일 전날까지가 한 기간", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    @Max(28)
    private Integer startDay;

    /** [request] 기본 예산 */
    @Schema(description = "기본 예산(원). 0 이상", example = "550000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(0)
    private Integer defaultBudget;
}
