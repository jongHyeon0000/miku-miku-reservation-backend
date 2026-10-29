package com.jong.mikumikureservationbackend.common.scheduler;

import com.jong.mikumikureservationbackend.service.budget.BudgetPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/* 매일 00시 20분에 끝난 예산 기간의 다음 기간을 만든다(이미지 정리 스케줄러들과 겹치지 않게 20분 뒤) */
@Component
@RequiredArgsConstructor
public class BudgetPeriodScheduler {

    private final BudgetPeriodService budgetPeriodService;

    @Scheduled(cron = "0 20 0 * * *")
    public void createNextPeriods() {
        budgetPeriodService.createNextPeriods();
    }
}
