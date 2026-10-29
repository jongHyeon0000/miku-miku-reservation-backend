package com.jong.mikumikureservationbackend.service.budget.impl;

import com.jong.mikumikureservationbackend.common.constant.FigureStatus;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.budget.BudgetAdjustMapper;
import com.jong.mikumikureservationbackend.mapper.budget.BudgetPeriodMapper;
import com.jong.mikumikureservationbackend.mapper.budget.BudgetSettingMapper;
import com.jong.mikumikureservationbackend.service.budget.BudgetPeriodService;
import com.jong.mikumikureservationbackend.service.budget.BudgetService;
import com.jong.mikumikureservationbackend.vo.budget.BudgetAdjustVO;
import com.jong.mikumikureservationbackend.vo.budget.BudgetPeriodVO;
import com.jong.mikumikureservationbackend.vo.budget.BudgetSettingVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetAdjustRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetPaymentCheckRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.request.BudgetSettingRequestVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetPaymentCheckResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetPeriodResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetResponseVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetSpendingVO;
import com.jong.mikumikureservationbackend.vo.figure.FigureVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {

    private final BudgetSettingMapper budgetSettingMapper;
    private final BudgetPeriodMapper budgetPeriodMapper;
    private final BudgetAdjustMapper budgetAdjustMapper;
    private final BudgetPeriodService budgetPeriodService;

    @Override
    public BudgetResponseVO getBudget(Long userId) {
        BudgetSettingVO setting = budgetSettingMapper.selectSetting(userId);
        LocalDate today = LocalDate.now();
        BudgetPeriodVO current = budgetPeriodMapper.selectByDate(userId, today);

        BudgetResponseVO response = new BudgetResponseVO();

        response.setEnabled(setting.getBudgetEnabled());
        response.setStartDay(setting.getBudgetStartDay());
        response.setDefaultBudget(setting.getDefaultBudget());
        response.setAdjusts(Collections.<BudgetAdjustVO>emptyList());
        response.setSpendings(Collections.<BudgetSpendingVO>emptyList());

        if (!setting.getBudgetEnabled()) {
            response.setResumable(current != null);

            return response;
        }

        response.setResumable(false);

        if (current == null) {
            return response;
        }

        List<BudgetAdjustVO> adjusts = budgetAdjustMapper.selectByPeriod(userId, current.getBudgetPeriodId());
        List<BudgetSpendingVO> spendings = budgetPeriodMapper.selectSpendings(userId, current.getStartDate(), current.getEndDate());

        response.setAdjusts(adjusts);
        response.setSpendings(spendings);
        response.setPeriod(toPeriodResponse(userId, current, spendings));

        return response;
    }

    @Override
    @Transactional
    public void start(Long userId, BudgetSettingRequestVO request) {
        BudgetSettingVO setting = budgetSettingMapper.selectSetting(userId);

        if (setting.getBudgetEnabled()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "이미 예산을 사용 중입니다");
        }

        budgetSettingMapper.updateSetting(userId, request.getStartDay(), request.getDefaultBudget());
        budgetSettingMapper.updateEnabled(userId, true);

        if (budgetPeriodMapper.selectByDate(userId, LocalDate.now()) == null) {
            budgetPeriodService.createCurrentPeriod(userId, request.getStartDay(), request.getDefaultBudget());
        }
    }

    @Override
    @Transactional
    public void changeEnabled(Long userId, boolean enabled) {
        if (enabled && budgetPeriodMapper.selectByDate(userId, LocalDate.now()) == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                    "이번 기간의 예산 기록이 없어 바로 켤 수 없습니다. 시작하기로 예산을 정해 주세요");
        }

        budgetSettingMapper.updateEnabled(userId, enabled);
    }

    @Override
    @Transactional
    public void changeSetting(Long userId, BudgetSettingRequestVO request) {
        requireEnabled(userId);

        budgetSettingMapper.updateSetting(userId, request.getStartDay(), request.getDefaultBudget());
    }

    @Override
    @Transactional
    public void addAdjust(Long userId, BudgetAdjustRequestVO request) {
        requireEnabled(userId);
        requireNonZero(request);

        BudgetPeriodVO current = budgetPeriodMapper.selectByDate(userId, LocalDate.now());

        if (current == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "이번 기간의 예산 기록이 없습니다");
        }

        budgetAdjustMapper.insert(userId, current.getBudgetPeriodId(), request.getAmount(), request.getMemo());
    }

    @Override
    @Transactional
    public void modifyAdjust(Long userId, Long budgetAdjustId, BudgetAdjustRequestVO request) {
        requireEnabled(userId);
        requireNonZero(request);
        requireCurrentPeriodAdjust(userId, budgetAdjustId);

        budgetAdjustMapper.update(userId, budgetAdjustId, request.getAmount(), request.getMemo());
    }

    @Override
    @Transactional
    public void deleteAdjust(Long userId, Long budgetAdjustId) {
        requireEnabled(userId);
        requireCurrentPeriodAdjust(userId, budgetAdjustId);

        budgetAdjustMapper.delete(userId, budgetAdjustId);
    }

    /*
     * 결제 금액 (상품마다)
     *  UNPURCHASED → RESERVED : 요청의 예약금(depositAmount)
     *  UNPURCHASED → PURCHASED: 가격 x 수량 (전액)
     *  RESERVED → PURCHASED   : 가격 x 수량 - 낸 예약금 (잔금)
     * 상품의 현재 상태로 전액과 잔금을 가르므로 결제 방식은 받지 않는다. 전이 규칙은 상태 전이 API와 같은 400으로 거절한다.
     */
    @Override
    public BudgetPaymentCheckResponseVO checkPayment(Long userId, BudgetPaymentCheckRequestVO request) {
        BudgetPaymentCheckResponseVO response = new BudgetPaymentCheckResponseVO();

        response.setExceeded(false);

        FigureStatus next = request.getStatus();

        if (next != FigureStatus.RESERVED && next != FigureStatus.PURCHASED) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "status는 RESERVED 또는 PURCHASED여야 합니다");
        }

        List<Long> figureIds = new ArrayList<>(new LinkedHashSet<>(request.getFigureIds()));

        if (next == FigureStatus.RESERVED && figureIds.size() != 1) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "예약금 결제는 상품 1개만 확인할 수 있습니다");
        }
        if (next == FigureStatus.RESERVED && request.getDepositAmount() == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "예약금(depositAmount)이 필요합니다");
        }

        Map<Long, FigureVO> figureById = new HashMap<>();

        for (FigureVO figure : budgetPeriodMapper.selectFiguresForCheck(userId, figureIds)) {
            figureById.put(figure.getFigureId(), figure);
        }

        long payAmount = 0;

        for (Long figureId : figureIds) {
            FigureVO figure = figureById.get(figureId);

            if (figure == null) {
                throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
            }
            if (!figure.getStatus().canMoveTo(next)) {
                throw new BusinessException(GlobalExceptionCode.INVALID_STATUS_TRANSITION,
                        figure.getStatus() + "에서 " + next + "(으)로는 변경할 수 없습니다");
            }

            payAmount += payAmountOf(figure, next, request.getDepositAmount());
        }

        BudgetSettingVO setting = budgetSettingMapper.selectSetting(userId);

        if (!setting.getBudgetEnabled()) {
            return response;
        }

        LocalDate today = LocalDate.now();
        LocalDate payDate = next == FigureStatus.RESERVED
                ? (request.getDepositPaidAt() == null ? today : request.getDepositPaidAt())
                : (request.getPaidAt() == null ? today : request.getPaidAt());
        BudgetPeriodVO period = budgetPeriodMapper.selectByDate(userId, payDate);

        response.setPayAmount((int) payAmount);

        if (period == null) {
            return response;
        }

        List<BudgetSpendingVO> spendings = budgetPeriodMapper.selectSpendings(userId, period.getStartDate(), period.getEndDate());
        int remaining = toPeriodResponse(userId, period, spendings).getRemaining();

        response.setRemaining(remaining);
        response.setRemainingAfter((int) (remaining - payAmount));
        response.setExceeded(payAmount > remaining);

        return response;
    }

    private long payAmountOf(FigureVO figure, FigureStatus next, Integer depositAmount) {
        if (next == FigureStatus.RESERVED) {
            return depositAmount;
        }

        long total = (long) figure.getPrice() * figure.getQuantity();

        if (figure.getStatus() == FigureStatus.RESERVED) {
            int paidDeposit = figure.getDepositAmount() == null ? 0 : figure.getDepositAmount();

            return Math.max(total - paidDeposit, 0);
        }

        return total;
    }

    /* 기간 한 줄에 증감 합계, 총 예산, 지출, 남은 돈을 계산해서 붙인다 */
    private BudgetPeriodResponseVO toPeriodResponse(Long userId, BudgetPeriodVO period, List<BudgetSpendingVO> spendings) {
        BudgetPeriodResponseVO response = new BudgetPeriodResponseVO();
        int adjustTotal = budgetAdjustMapper.selectSumByPeriod(userId, period.getBudgetPeriodId());
        int spent = 0;

        for (BudgetSpendingVO spending : spendings) {
            spent += spending.getAmount();
        }

        response.setBudgetPeriodId(period.getBudgetPeriodId());
        response.setStartDate(period.getStartDate());
        response.setEndDate(period.getEndDate());
        response.setAmount(period.getAmount());
        response.setAdjustTotal(adjustTotal);
        response.setTotalBudget(period.getAmount() + adjustTotal);
        response.setSpent(spent);
        response.setRemaining(period.getAmount() + adjustTotal - spent);

        return response;
    }

    private void requireEnabled(Long userId) {
        if (!budgetSettingMapper.selectSetting(userId).getBudgetEnabled()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "예산을 사용 중일 때만 할 수 있습니다");
        }
    }

    private void requireNonZero(BudgetAdjustRequestVO request) {
        if (request.getAmount() == 0) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "amount: 0은 입력할 수 없습니다");
        }
    }

    /* 없는 줄이면 404, 이번 기간의 줄이 아니면 400 */
    private void requireCurrentPeriodAdjust(Long userId, Long budgetAdjustId) {
        BudgetAdjustVO adjust = budgetAdjustMapper.selectById(userId, budgetAdjustId);

        if (adjust == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        BudgetPeriodVO current = budgetPeriodMapper.selectByDate(userId, LocalDate.now());

        if (current == null || !current.getBudgetPeriodId().equals(adjust.getBudgetPeriodId())) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "이번 기간의 증감만 수정하거나 삭제할 수 있습니다");
        }
    }
}
