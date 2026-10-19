package com.jong.mikumikureservationbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    @NotBlank
    private final String fromAddress;

    @NotBlank
    private final String fromName;

    public MailProperties(String fromAddress, String fromName) {
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }
}
