package com.jong.mikumikureservationbackend.controller.budget.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetAdjustRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetEnabledRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetPaymentCheckRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetSettingRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetPaymentCheckResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* BudgetController의 Swagger 설명 전용 인터페이스. 로직은 BudgetController에만 둔다 */
@Tag(name = "예산", description = "예산 관리. 기간(시작일부터 다음 달 시작일 전날)마다 기본 예산이 자동으로 부여되고, 그 기간에 증감(+, -)을 넣을 수 있다. "
        + "지출과 남은 돈은 저장하지 않고 상품의 결제일로 계산한다. 모든 조회와 변경은 현재 사용자의 것만 대상으로 한다.")
public interface BudgetControllerDocs {

    @Operation(
            summary = "예산 현황 조회 (예: 이번 기간 남은 돈, 증감, 지출 목록)",
            description = "페이지에 들어올 때 부른다. 응답의 `enabled`와 `resumable`로 화면을 고른다.\n\n"
                    + "- `enabled = true`: 설정, 이번 기간 현황(`period`), 증감 목록(`adjusts`), 지출 목록(`spendings`)을 돌려준다.\n"
                    + "- `enabled = false`, `resumable = true`: 이번 기간 기록이 남아 있어 입력 없이 다시 켤 수 있다(`PATCH /api/budget/enabled`).\n"
                    + "- `enabled = false`, `resumable = false`: 처음 쓰거나 기간이 지나 기록이 없다. 소개 화면에서 `POST /api/budget/start`로 시작한다. "
                    + "`startDay`, `defaultBudget`에 마지막 설정값(처음이면 1, 0)이 들어 있어 입력칸을 미리 채울 수 있다.\n"
                    + "- 꺼져 있으면 `period`는 null이고 `adjusts`, `spendings`는 빈 배열이다.\n\n"
                    + "지출은 상품의 결제일이 기간 안에 있는 결제 금액이다(예약금은 예약금 결제일, 전액과 잔금은 결제일). 장바구니, 미구매는 지출이 아니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예산 현황")
    })
    ApiResponse<BudgetResponseVO> getBudget();

    @Operation(
            summary = "예산 시작하기 (예: 매월 25일, 기본 예산 55만원)",
            description = "설정을 저장하고 예산을 켠다. 오늘이 속한 기간이 없으면 바로 만든다(처음 쓰는 사용자, 또는 껐다가 기간이 지나 돌아온 사용자).\n\n"
                    + "- 시작일(`startDay`)은 1~28이다. 시작일부터 다음 달 시작일 전날까지가 한 기간이다.\n"
                    + "- 이미 켜져 있으면 400이다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "시작 예", value = "{\"startDay\":25,\"defaultBudget\":550000}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시작됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 켜져 있음, 입력값 오류")
    })
    ApiResponse<Void> start(BudgetSettingRequestVO request);

    @Operation(
            summary = "예산 켜기, 끄기 (예: 껐다가 이번 기간 안에 다시 켜기)",
            description = "`enabled = true` 켜기는 **이번 기간의 기록이 남아 있을 때만** 된다(입력 없이 한 번에 켜짐). 기록이 없으면 400이고 `POST /api/budget/start`를 써야 한다.\n\n"
                    + "`enabled = false` 끄기는 기록을 지우지 않고 기능만 끈다. 꺼져 있는 동안은 새 기간이 만들어지지 않는다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "켜기", value = "{\"enabled\":true}"),
            @ExampleObject(name = "끄기", value = "{\"enabled\":false}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "켜려는데 이번 기간 기록이 없음, 입력값 오류")
    })
    ApiResponse<Void> changeEnabled(BudgetEnabledRequestVO request);

    @Operation(
            summary = "예산 설정 변경 (예: 기본 예산 20만원에서 30만원으로)",
            description = "시작일과 기본 예산을 **둘 다** 보낸다. 이미 시작된 기간은 그대로이고, 다음 기간부터 새 값이 적용된다. 예산을 쓰는 중일 때만 된다(꺼져 있으면 400).")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "변경 예", value = "{\"startDay\":25,\"defaultBudget\":300000}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "예산이 꺼져 있음, 입력값 오류")
    })
    ApiResponse<Void> changeSetting(BudgetSettingRequestVO request);

    @Operation(
            summary = "증감 추가 (예: 보너스 +10만원, 경조사 -3만원)",
            description = "이번 기간에 증감 한 줄을 넣는다. 금액은 0이 아닌 정수이고 더하면 양수, 빼려면 음수로 보낸다. "
                    + "그 기간에만 쓰이고 다음 기간으로 넘어가지 않는다. 예산을 쓰는 중일 때만 된다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "더하기", value = "{\"amount\":100000,\"memo\":\"보너스\"}"),
            @ExampleObject(name = "빼기", value = "{\"amount\":-30000,\"memo\":\"경조사\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "예산이 꺼져 있음, 이번 기간 없음, 0원, 입력값 오류")
    })
    ApiResponse<Void> addAdjust(BudgetAdjustRequestVO request);

    @Operation(
            summary = "증감 수정 (예: 보너스 10만원을 12만원으로)",
            description = "이번 기간의 증감 한 줄을 통째로 덮어쓴다(금액과 메모를 항상 같이 보낸다. 메모를 비우면 지워진다). 지난 기간의 줄은 수정할 수 없다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "예산이 꺼져 있음, 지난 기간의 줄, 0원, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> modifyAdjust(
            @Parameter(description = "수정할 증감 id", example = "5") Long budgetAdjustId,
            BudgetAdjustRequestVO request);

    @Operation(
            summary = "증감 삭제 (예: 잘못 넣은 증감)",
            description = "이번 기간의 증감 한 줄을 지운다. 지난 기간의 줄은 지울 수 없다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제됨 (응답 본문 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "예산이 꺼져 있음, 지난 기간의 줄"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> deleteAdjust(
            @Parameter(description = "삭제할 증감 id", example = "5") Long budgetAdjustId);

    @Operation(
            summary = "결제 전 예산 확인 (예: 구매완료로 넘기기 전에 예산을 넘는지)",
            description = "미구매 → 예약완료/구매완료, 예약완료 → 구매완료(잔금)로 **넘기기 전에** 부른다. 아무것도 바꾸지 않고 결제일이 속한 기간의 남은 돈과 결제 금액만 비교한다. "
                    + "경고는 막지 않는다. `exceeded = true`면 프론트가 경고창을 띄우고, 사용자가 진행하면 기존 상태 전이 API를 부른다.\n\n"
                    + "결제 금액은 상품마다 다음과 같다.\n"
                    + "- 미구매 → 예약완료: 요청의 예약금(`depositAmount`). 예약금 결제는 상품 1개만 확인할 수 있다.\n"
                    + "- 미구매 → 구매완료(전액): 가격 x 수량\n"
                    + "- 예약완료 → 구매완료(잔금): 가격 x 수량 - 낸 예약금\n\n"
                    + "예산이 꺼져 있거나 결제일이 속한 기간이 없으면 `exceeded = false`이다(금액은 null일 수 있다). 전이 규칙에 맞지 않으면 상태 전이 API와 같은 400이다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "일괄, 구매완료", value = "{\"figureIds\":[31,32],\"status\":\"PURCHASED\",\"paidAt\":\"2026-10-28\"}"),
            @ExampleObject(name = "상세, 예약완료", value = "{\"figureIds\":[31],\"status\":\"RESERVED\",\"depositAmount\":20000,\"depositPaidAt\":\"2026-10-30\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "초과 여부와 금액"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "허용되지 않는 상태 변경, 예약금 누락, 예약금 결제를 여러 개로 요청, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 상품이거나 내 상품이 아님")
    })
    ApiResponse<BudgetPaymentCheckResponseVO> checkPayment(BudgetPaymentCheckRequestVO request);
}
