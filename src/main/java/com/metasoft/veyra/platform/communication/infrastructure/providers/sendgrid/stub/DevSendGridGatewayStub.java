package com.metasoft.veyra.platform.communication.infrastructure.providers.sendgrid.stub;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.sendgrid.SendGridGateway;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendEmailCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.sendgrid.enabled", havingValue = "false")
public class DevSendGridGatewayStub implements SendGridGateway {

    @Override
    public void send(SendEmailCommand command) {
        log.info("SendGrid integration disabled. Email not sent (to={}, subject={})",
                command.to(), command.subject());
    }
}
