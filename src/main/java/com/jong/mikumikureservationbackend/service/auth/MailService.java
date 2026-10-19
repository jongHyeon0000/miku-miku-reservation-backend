package com.jong.mikumikureservationbackend.service.auth;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;

public interface MailService {

    /* 인증 코드 메일을 보낸다. 발송에 실패하면 MAIL_SEND_FAILED(500)를 던진다 */
    void sendVerificationCode(String to, EmailVerificationPurpose purpose, String code, int expireMinutes);
}
