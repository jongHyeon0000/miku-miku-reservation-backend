package com.jong.mikumikureservationbackend.service.budget;

import com.jong.mikumikureservationbackend.vo.budget.request.BudgetAdjustRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetPaymentCheckRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetSettingRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetPaymentCheckResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetResponseVO;

public interface BudgetService {

    /* 예산 현황. 꺼져 있으면 resumable과 마지막 설정값만 채워 돌려준다 */
    BudgetResponseVO getBudget(Long userId);

    /* 설정을 저장하고 예산을 켠다. 오늘이 속한 기간이 없으면 만든다. 이미 켜져 있으면 400 */
    void start(Long userId, BudgetSettingRequestVO request);

    /* 켜기는 이번 기간 기록이 있을 때만 된다(없으면 400, 시작하기를 써야 한다). 끄기는 기록을 남긴 채 기능만 끈다 */
    void changeEnabled(Long userId, boolean enabled);

    /* 시작일, 기본 예산 변경. 이번 기간은 그대로이고 다음 기간부터 적용된다. 꺼져 있으면 400 */
    void changeSetting(Long userId, BudgetSettingRequestVO request);

    /* 이번 기간에 증감 한 줄을 넣는다. 꺼져 있거나 이번 기간이 없으면 400 */
    void addAdjust(Long userId, BudgetAdjustRequestVO request);

    /* 이번 기간의 증감 한 줄을 통째로 덮어쓴다. 지난 기간의 줄이면 400, 없는 줄이면 404 */
    void modifyAdjust(Long userId, Long budgetAdjustId, BudgetAdjustRequestVO request);

    /* 이번 기간의 증감 한 줄을 지운다. 지난 기간의 줄이면 400, 없는 줄이면 404 */
    void deleteAdjust(Long userId, Long budgetAdjustId);

    /* 결제로 넘기기 전에 결제일이 속한 기간의 남은 돈과 결제 금액을 비교한다. 아무것도 바꾸지 않는다 */
    BudgetPaymentCheckResponseVO checkPayment(Long userId, BudgetPaymentCheckRequestVO request);
}
