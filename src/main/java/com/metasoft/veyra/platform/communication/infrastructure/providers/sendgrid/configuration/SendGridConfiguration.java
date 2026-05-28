package com.metasoft.veyra.platform.communication.infrastructure.providers.sendgrid.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfiguration {

    @Value("${sendgrid.api.key:}")
    private String apiKey;

    @Value("${sendgrid.from.email:}")
    private String fromEmail;

    @Bean
    SendGridSettings sendGridSettings() {
        return new SendGridSettings(apiKey, fromEmail);
    }
}
