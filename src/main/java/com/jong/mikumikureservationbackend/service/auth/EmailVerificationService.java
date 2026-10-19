package com.jong.mikumikureservationbackend.service.auth;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;

/* 가입, 아이디 찾기, 비밀번호 재설정이 함께 쓰는 공통 인증. 어떤 업무인지는 모르고, 업무별 대상 검사는 호출하는 쪽이 한다 */
public interface EmailVerificationService {

    /* 6자리 코드를 만들어 저장하고 메일로 보낸다. 같은 이메일, 목적은 60초 뒤에 다시 받을 수 있다 */
    void sendCode(String email, EmailVerificationPurpose purpose);

    /* 코드가 맞으면 인증 완료로 표시한다. 틀리면 횟수가 쌓이고 5회가 넘으면 다시 발송받아야 한다 */
    void verify(String email, EmailVerificationPurpose purpose, String code);

    /* 인증 완료된 이메일인지 확인하고 1회용으로 소모한다. 안 됐으면 400 */
    void consume(String email, EmailVerificationPurpose purpose);
}
