package com.metasoft.veyra.platform.communication.infrastructure.providers.sendgrid.services;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.sendgrid.SendGridGateway;
import com.metasoft.veyra.platform.communication.domain.exceptions.CommunicationProviderNotConfiguredException;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendEmailCommand;
import com.metasoft.veyra.platform.communication.infrastructure.providers.sendgrid.configuration.SendGridSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.sendgrid.enabled", havingValue = "true")
public class SendGridGatewayImpl implements SendGridGateway {

    private final SendGridSettings sendGridSettings;

    public SendGridGatewayImpl(SendGridSettings sendGridSettings) {
        this.sendGridSettings = sendGridSettings;
    }

    @Override
    public void send(SendEmailCommand command) {
        if (sendGridSettings.apiKey() == null || sendGridSettings.apiKey().isBlank()) {
            throw new CommunicationProviderNotConfiguredException("SendGrid");
        }

        log.info("SendGrid integration is configured. Email sending is currently disabled (to={}, subject={})",
                command.to(), command.subject());
    }
}
