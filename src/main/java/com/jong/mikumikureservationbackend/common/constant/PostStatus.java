package com.jong.mikumikureservationbackend.common.constant;

/* 커뮤니티 글과 댓글의 상태. 삭제와 탈퇴는 행을 지우지 않고 이 값만 바꾼다 */
public enum PostStatus {

    NORMAL,
    DELETED,
    WITHDRAWN
}
