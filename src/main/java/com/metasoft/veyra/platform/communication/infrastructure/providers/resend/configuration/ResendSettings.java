package com.metasoft.veyra.platform.communication.infrastructure.providers.resend.configuration;

public record ResendSettings(
        String apiKey,
        String fromEmail,
        String fromName
) {
}
