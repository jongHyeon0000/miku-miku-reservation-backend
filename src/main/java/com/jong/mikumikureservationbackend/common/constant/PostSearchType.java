package com.jong.mikumikureservationbackend.common.constant;

/* 커뮤니티 글 검색 대상. 제목, 내용은 부분 일치, 사용자명은 완전 일치 */
public enum PostSearchType {

    TITLE_CONTENT,
    TITLE,
    CONTENT,
    AUTHOR
}
