package com.jong.mikumikureservationbackend.service.budget.impl;

import com.jong.mikumikureservationbackend.mapper.budget.BudgetPeriodMapper;
import com.jong.mikumikureservationbackend.service.budget.BudgetPeriodService;
import com.jong.mikumikureservationbackend.vo.budget.BudgetPeriodTargetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetPeriodServiceImpl implements BudgetPeriodService {

    private final BudgetPeriodMapper budgetPeriodMapper;

    @Override
    @Transactional
    public void createCurrentPeriod(Long userId, int startDay, int amount) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.getDayOfMonth() >= startDay
                ? today.withDayOfMonth(startDay)
                : today.minusMonths(1).withDayOfMonth(startDay);
        LocalDate lastEnd = budgetPeriodMapper.selectLastEndDate(userId);

        // 껐다가 돌아온 경우 지난 기간과 겹치면 지출이 두 기간에 중복 집계되므로 지난 기간 다음 날부터 시작한다
        if (lastEnd != null && !lastEnd.isBefore(start)) {
            start = lastEnd.plusDays(1);
        }

        budgetPeriodMapper.insert(userId, start, periodEnd(start, startDay), amount);
    }

    @Override
    @Transactional
    public void createNextPeriods() {
        LocalDate today = LocalDate.now();

        for (BudgetPeriodTargetVO target : budgetPeriodMapper.selectEndedTargets(today)) {
            LocalDate start = target.getLastEndDate().plusDays(1);

            budgetPeriodMapper.insert(target.getUserId(), start,
                    periodEnd(start, target.getBudgetStartDay()), target.getDefaultBudget());
        }
    }

    /* 시작일 이후 처음 오는 startDay 날짜의 전날 (startDay는 1~28이라 모든 달에 있다) */
    private LocalDate periodEnd(LocalDate start, int startDay) {
        LocalDate next = start.withDayOfMonth(startDay);

        if (!next.isAfter(start)) {
            next = next.plusMonths(1);
        }

        return next.minusDays(1);
    }
}
