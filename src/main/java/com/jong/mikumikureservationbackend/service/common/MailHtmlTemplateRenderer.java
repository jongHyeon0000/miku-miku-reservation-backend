package com.jong.mikumikureservationbackend.service.common;

import com.jong.mikumikureservationbackend.common.constant.EmailVerificationPurpose;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/* 메일 HTML 템플릿(resources/mail)을 읽어 자리표시자를 채운다. 파일은 기동할 때 한 번 읽고, 없으면 기동에 실패한다 */
@Component
public class MailHtmlTemplateRenderer {

    private final String layout;
    private final String verification;

    public MailHtmlTemplateRenderer() {
        this.layout = read("mail/layout.html");
        this.verification = read("mail/verification.html");
    }

    public String renderVerification(EmailVerificationPurpose purpose, String code, int expireMinutes) {
        String content = verification
                .replace("{{purposeLabel}}", purpose.getLabel())
                .replace("{{code}}", code)
                .replace("{{expireMinutes}}", String.valueOf(expireMinutes));

        return layout.replace("{{content}}", content);
    }

    private static String read(String path) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("메일 템플릿을 읽을 수 없습니다: " + path, e);
        }
    }
}
