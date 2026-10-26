package com.jong.mikumikureservationbackend.service.genre;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;

public interface GenreCharacterService {

    PageResponseVO<GenreCharacterVO> getPage(Long userId, Long groupId, int page, int size);

    GenreCharacterVO create(Long userId, GenreCharacterVO vo);

    void update(Long userId, Long genreCharacterId, GenreCharacterVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long genreCharacterId);

    UsageResponseVO getUsage(Long userId, Long genreCharacterId);
}
