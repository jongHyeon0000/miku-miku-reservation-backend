package com.jong.mikumikureservationbackend.service.series;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;

public interface SeriesService {

    PageResponseVO<SeriesVO> getPage(Long userId, Long groupId, int page, int size);

    SeriesVO create(Long userId, SeriesVO vo);

    void update(Long userId, Long seriesId, SeriesVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long seriesId);

    UsageResponseVO getUsage(Long userId, Long seriesId);
}
