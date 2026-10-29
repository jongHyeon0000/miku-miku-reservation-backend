package com.jong.mikumikureservationbackend.vo.budget.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 증감 추가와 수정 요청 본문. 수정은 두 값을 항상 같이 받아 그 줄을 통째로 덮어쓴다 */
@Schema(description = "예산 증감 요청 본문 (추가, 수정 공용)")
@Getter
@Setter
@NoArgsConstructor
public class BudgetAdjustRequestVO {

    /** [request] 증감 금액 */
    @Schema(description = "증감 금액(원). 0이 아닌 정수. 더하면 양수, 빼려면 음수", example = "-30000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer amount;

    /** [request] 이유 메모 */
    @Schema(description = "이유 메모. 100자 이하. 수정에서 비우면 메모가 지워진다", example = "경조사")
    @Size(max = 100)
    private String memo;
}
