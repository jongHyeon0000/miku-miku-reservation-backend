package com.jong.mikumikureservationbackend.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailVerificationPurpose {

    SIGNUP("회원가입"),
    FIND_ID("아이디 찾기"),
    RESET_PASSWORD("비밀번호 재설정");

    private final String label;
}
