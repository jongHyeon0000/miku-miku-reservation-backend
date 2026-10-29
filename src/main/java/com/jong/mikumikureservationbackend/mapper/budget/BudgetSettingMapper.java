package com.jong.mikumikureservationbackend.mapper.budget;

import com.jong.mikumikureservationbackend.vo.budget.BudgetSettingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/* users 테이블의 예산 설정 3컬럼(budget_enabled, budget_start_day, default_budget)만 다룬다 */
@Mapper
public interface BudgetSettingMapper {

    BudgetSettingVO selectSetting(@Param("userId") Long userId);

    int updateSetting(@Param("userId") Long userId, @Param("startDay") Integer startDay,
                      @Param("defaultBudget") Integer defaultBudget);

    int updateEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
}
