package com.jong.mikumikureservationbackend.service.post;

public interface PostCommentImageCleanupService {

    /* 업로드 후 보관 일수가 지났는데 어느 댓글에도 연결되지 않은 댓글 이미지를 파일과 행 모두 지운다 */
    void cleanup();
}
