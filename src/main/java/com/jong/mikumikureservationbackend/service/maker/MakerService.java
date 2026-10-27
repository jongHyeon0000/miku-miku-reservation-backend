package com.jong.mikumikureservationbackend.service.maker;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.maker.MakerVO;

import java.util.List;

public interface MakerService {

    List<MakerVO> getAll(Long userId);

    PageResponseVO<MakerVO> getPage(Long userId, int page, int size);

    MakerVO create(Long userId, MakerVO vo);

    void update(Long userId, Long makerId, MakerVO vo);

    /* 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long makerId);

    UsageResponseVO getUsage(Long userId, Long makerId);

    /* 하나라도 없으면 전체를 되돌린다. 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    void deleteAll(Long userId, List<Long> makerIds);
}
