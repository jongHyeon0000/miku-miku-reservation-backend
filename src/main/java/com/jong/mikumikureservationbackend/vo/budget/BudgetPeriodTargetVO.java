package com.jong.mikumikureservationbackend.vo.budget;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/* 스케줄러가 다음 기간을 만들 대상 (마지막 기간이 끝난 사용자, 매퍼 결과 전용) */
@Getter
@Setter
@NoArgsConstructor
public class BudgetPeriodTargetVO {

    private Long userId;

    /** 예산 기간 시작일 (1~28) */
    private Integer budgetStartDay;

    /** 새 기간에 부여할 기본 예산(원) */
    private Integer defaultBudget;

    /** 마지막 기간의 종료일 */
    private LocalDate lastEndDate;
}
