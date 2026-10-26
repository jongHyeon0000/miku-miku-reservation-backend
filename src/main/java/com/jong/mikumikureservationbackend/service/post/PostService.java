package com.jong.mikumikureservationbackend.service.post;

import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;

public interface PostService {

    /* 최신순 목록(본문 제외). 삭제, 탈퇴한 글은 내용 없이 자리만 남긴다 */
    PageResponseVO<PostVO> getPage(Long userId, int page, int size);

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
