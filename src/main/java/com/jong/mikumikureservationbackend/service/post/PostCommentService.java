package com.jong.mikumikureservationbackend.service.post;

import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;

import java.util.List;

public interface PostCommentService {

    /* 그 글의 댓글 전체(오래된 순). 최상위 댓글마다 replies에 답글을 담는다. 삭제, 탈퇴한 댓글도 자리에 남고 내용은 가린다. 없거나 삭제, 탈퇴한 글은 404 */
    List<PostCommentVO> getList(Long userId, Long postId);

    /* 댓글 또는 답글 등록. 이미지는 먼저 업로드해서 받은 id로 연결한다 */
    PostCommentVO create(Long userId, Long postId, PostCommentVO request);

    /* 본인 댓글만(남의 댓글 403, 없거나 삭제, 탈퇴한 댓글 404). 내용과 이미지 최종 목록만 바꾼다 */
    PostCommentVO update(Long userId, Long postCommentId, PostCommentVO request);

    /* 본인 댓글만. 행을 지우지 않고 DELETED로 바꾼다(답글은 그대로) */
    void delete(Long userId, Long postCommentId);

    /* 탈퇴 직전에 호출: 이 사용자의 정상 댓글을 WITHDRAWN으로 바꾼다 */
    void withdrawByUser(Long userId);
}
