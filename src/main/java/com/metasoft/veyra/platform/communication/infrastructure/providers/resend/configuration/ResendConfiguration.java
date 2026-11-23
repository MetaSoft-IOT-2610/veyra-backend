package com.metasoft.veyra.platform.communication.infrastructure.providers.resend.configuration;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendConfiguration {

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.from.email:}")
    private String fromEmail;

    @Value("${resend.from.name:}")
    private String fromName;

    @Bean
    ResendSettings resendSettings() {
        return new ResendSettings(apiKey, fromEmail, fromName);
    }

    @Bean
    @ConditionalOnProperty(name = "integrations.resend.enabled", havingValue = "true")
    Resend resend(ResendSettings resendSettings) {
        return new Resend(resendSettings.apiKey());
    }
}
