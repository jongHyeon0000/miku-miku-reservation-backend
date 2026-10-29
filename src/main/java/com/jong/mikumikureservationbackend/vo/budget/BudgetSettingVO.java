package com.jong.mikumikureservationbackend.vo.budget;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* users 테이블의 예산 설정 3컬럼 (매퍼 결과 전용, 응답으로 직접 내보내지 않는다) */
@Getter
@Setter
@NoArgsConstructor
public class BudgetSettingVO {

    /** 예산 사용 여부 (0 사용 안 함, 1 사용 중) */
    private Boolean budgetEnabled;

    /** 예산 기간 시작일 (1~28) */
    private Integer budgetStartDay;

    /** 새 기간에 부여할 기본 예산(원) */
    private Integer defaultBudget;
}
