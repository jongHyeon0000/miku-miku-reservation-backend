package com.jong.mikumikureservationbackend.service.type;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeVO;

import java.util.List;

public interface FigureTypeService {

    PageResponseVO<FigureTypeVO> getPage(Long userId, Long groupId, int page, int size);

    FigureTypeVO create(Long userId, FigureTypeVO vo);

    void update(Long userId, Long figureTypeId, FigureTypeVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long figureTypeId);

    UsageResponseVO getUsage(Long userId, Long figureTypeId);

    /* 하나라도 없으면 전체를 되돌린다. 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    void deleteAll(Long userId, List<Long> figureTypeIds);
}
