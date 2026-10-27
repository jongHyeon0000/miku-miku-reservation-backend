package com.jong.mikumikureservationbackend.service.genre;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;

import java.util.List;

public interface GenreCharacterService {

    PageResponseVO<GenreCharacterVO> getPage(Long userId, Long groupId, int page, int size);

    GenreCharacterVO create(Long userId, GenreCharacterVO vo);

    void update(Long userId, Long genreCharacterId, GenreCharacterVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long genreCharacterId);

    UsageResponseVO getUsage(Long userId, Long genreCharacterId);

    /* 하나라도 없으면 전체를 되돌린다. 선택한 항목만 가지고 있던 상품에는 기본 항목(기타)을 연결한 뒤 삭제한다 */
    void deleteAll(Long userId, List<Long> genreCharacterIds);
}
