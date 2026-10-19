package com.jong.mikumikureservationbackend.config;

import com.jong.mikumikureservationbackend.config.properties.CorsProperties;
import com.jong.mikumikureservationbackend.config.properties.JwtProperties;
import com.jong.mikumikureservationbackend.config.properties.MailProperties;
import com.jong.mikumikureservationbackend.config.properties.SwaggerProperties;
import com.jong.mikumikureservationbackend.config.properties.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        UploadProperties.class,
        CorsProperties.class,
        JwtProperties.class,
        MailProperties.class,
        SwaggerProperties.class
})
public class PropertiesConfig {
}
