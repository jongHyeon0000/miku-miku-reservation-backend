package com.jong.mikumikureservationbackend.vo.budget.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 예산 켜기, 끄기 요청 본문 */
@Schema(description = "예산 켜기, 끄기 요청 본문")
@Getter
@Setter
@NoArgsConstructor
public class BudgetEnabledRequestVO {

    /** [request] true 켜기, false 끄기 */
    @Schema(description = "true 켜기, false 끄기", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean enabled;
}
