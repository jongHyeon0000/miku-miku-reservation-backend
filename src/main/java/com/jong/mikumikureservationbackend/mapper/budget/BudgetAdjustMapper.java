package com.jong.mikumikureservationbackend.mapper.budget;

import com.jong.mikumikureservationbackend.vo.budget.BudgetAdjustVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetAdjustMapper {

    List<BudgetAdjustVO> selectByPeriod(@Param("userId") Long userId, @Param("budgetPeriodId") Long budgetPeriodId);

    Integer selectSumByPeriod(@Param("userId") Long userId, @Param("budgetPeriodId") Long budgetPeriodId);

    BudgetAdjustVO selectById(@Param("userId") Long userId, @Param("budgetAdjustId") Long budgetAdjustId);

    int insert(@Param("userId") Long userId, @Param("budgetPeriodId") Long budgetPeriodId,
               @Param("amount") Integer amount, @Param("memo") String memo);

    int update(@Param("userId") Long userId, @Param("budgetAdjustId") Long budgetAdjustId,
               @Param("amount") Integer amount, @Param("memo") String memo);

    int delete(@Param("userId") Long userId, @Param("budgetAdjustId") Long budgetAdjustId);
}
