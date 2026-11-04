package com.jong.mikumikureservationbackend.service.post;

import com.jong.mikumikureservationbackend.common.constant.PostSearchType;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;

import java.util.List;

public interface PostService {

    /* 최신순 목록(본문 제외). 삭제, 탈퇴한 글은 내용 없이 자리만 남긴다. keyword나 authorId가 있으면 정상 글만 검색한다(searchType이 없으면 제목+내용, authorId는 user id 완전 일치) */
    PageResponseVO<PostVO> getPage(Long userId, Long authorId, PostSearchType searchType, String keyword, int page, int size);

    /* 그 사용자의 정상 글 최근 size개(최신순, 본문 제외). 없거나 탈퇴한 사용자는 빈 목록 */
    List<PostVO> getRecentByUser(Long userId, Long targetUserId, int size);

    /* 상세. 호출마다 조회수가 1 오르고 응답은 오른 뒤 값이다. 삭제, 탈퇴한 글은 404 */
    PostVO getDetail(Long userId, Long postId);

    PostVO create(Long userId, PostVO request);

    /* 본인 글만(남의 글 403, 없거나 삭제, 탈퇴한 글 404) */
    PostVO update(Long userId, Long postId, PostVO request);

    /* 본인 글만. 행을 지우지 않고 DELETED로 바꾼다 */
    void delete(Long userId, Long postId);

    /* 탈퇴 직전에 호출: 이 사용자의 정상 글을 WITHDRAWN으로 바꾼다 */
    void withdrawByUser(Long userId);
}
