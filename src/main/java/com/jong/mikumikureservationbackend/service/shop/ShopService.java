package com.jong.mikumikureservationbackend.service.shop;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopVO;

public interface ShopService {

    PageResponseVO<ShopVO> getPage(Long userId, Long groupId, int page, int size);

    ShopVO create(Long userId, ShopVO vo);

    void update(Long userId, Long shopId, ShopVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long shopId);

    UsageResponseVO getUsage(Long userId, Long shopId);
}
