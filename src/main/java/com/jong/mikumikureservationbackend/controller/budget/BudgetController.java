package com.jong.mikumikureservationbackend.controller.budget;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.budget.doc.BudgetControllerDocs;
import com.jong.mikumikureservationbackend.service.budget.BudgetService;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetAdjustRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetEnabledRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetPaymentCheckRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetSettingRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetPaymentCheckResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetResponseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController implements BudgetControllerDocs {

    private final BudgetService budgetService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<BudgetResponseVO> getBudget() {
        return ApiResponse.ok("조회가 완료되었습니다.", budgetService.getBudget(currentUser.getId()));
    }

    @Override
    @PostMapping("/start")
    public ApiResponse<Void> start(@Valid @RequestBody BudgetSettingRequestVO request) {
        budgetService.start(currentUser.getId(), request);

        return ApiResponse.ok("예산 관리를 시작했습니다.");
    }

    @Override
    @PatchMapping("/enabled")
    public ApiResponse<Void> changeEnabled(@Valid @RequestBody BudgetEnabledRequestVO request) {
        budgetService.changeEnabled(currentUser.getId(), request.getEnabled());

        return ApiResponse.ok("변경이 완료되었습니다.");
    }

    @Override
    @PutMapping("/setting")
    public ApiResponse<Void> changeSetting(@Valid @RequestBody BudgetSettingRequestVO request) {
        budgetService.changeSetting(currentUser.getId(), request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @PostMapping("/adjust")
    public ApiResponse<Void> addAdjust(@Valid @RequestBody BudgetAdjustRequestVO request) {
        budgetService.addAdjust(currentUser.getId(), request);

        return ApiResponse.ok("저장이 완료되었습니다.");
    }

    @Override
    @PatchMapping("/adjust/{budgetAdjustId}")
    public ApiResponse<Void> modifyAdjust(
            @PathVariable Long budgetAdjustId,
            @Valid @RequestBody BudgetAdjustRequestVO request) {
        budgetService.modifyAdjust(currentUser.getId(), budgetAdjustId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/adjust/{budgetAdjustId}")
    public ApiResponse<Void> deleteAdjust(@PathVariable Long budgetAdjustId) {
        budgetService.deleteAdjust(currentUser.getId(), budgetAdjustId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @PostMapping("/payment-check")
    public ApiResponse<BudgetPaymentCheckResponseVO> checkPayment(@Valid @RequestBody BudgetPaymentCheckRequestVO request) {
        return ApiResponse.ok("조회가 완료되었습니다.", budgetService.checkPayment(currentUser.getId(), request));
    }
}
