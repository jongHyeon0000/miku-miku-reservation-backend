package com.jong.mikumikureservationbackend.mapper.budget;

import com.jong.mikumikureservationbackend.vo.budget.BudgetPeriodTargetVO;
import com.jong.mikumikureservationbackend.vo.budget.BudgetPeriodVO;
import com.jong.mikumikureservationbackend.vo.budget.response.BudgetSpendingVO;
import com.jong.mikumikureservationbackend.vo.figure.FigureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BudgetPeriodMapper {

    /* 그 날짜가 속한 기간. 없으면 null */
    BudgetPeriodVO selectByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /* 마지막 기간의 종료일. 기간이 하나도 없으면 null */
    LocalDate selectLastEndDate(@Param("userId") Long userId);

    int insert(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
               @Param("endDate") LocalDate endDate, @Param("amount") Integer amount);

    /* 예산을 쓰는 활성 사용자 중 마지막 기간이 오늘 이전에 끝난 사용자 */
    List<BudgetPeriodTargetVO> selectEndedTargets(@Param("today") LocalDate today);

    /* 결제일이 [startDate, endDate]에 속하는 결제 한 번마다 한 줄 (최근 결제일 순) */
    List<BudgetSpendingVO> selectSpendings(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /* 결제 전 확인에 필요한 상품의 상태, 가격, 수량, 낸 예약금 */
    List<FigureVO> selectFiguresForCheck(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);
}
