package com.jong.mikumikureservationbackend.service.budget;

public interface BudgetPeriodService {

    /*
     * 오늘이 속한 기간을 만든다. 기간 시작일은 오늘 이전에 가장 최근에 온 시작일(startDay)이고,
     * 이미 있는 기간과 겹치면 그 기간 다음 날부터 시작한다. 종료일은 시작일 이후 처음 오는 startDay의 전날이다.
     */
    void createCurrentPeriod(Long userId, int startDay, int amount);

    /*
     * 스케줄러가 부른다. 예산을 쓰는 활성 사용자 중 마지막 기간이 오늘 이전에 끝난 사용자마다
     * 다음 기간(끝난 다음 날부터)을 만든다. 기본 예산과 시작일은 그때의 사용자 설정값을 쓴다.
     */
    void createNextPeriods();
}
