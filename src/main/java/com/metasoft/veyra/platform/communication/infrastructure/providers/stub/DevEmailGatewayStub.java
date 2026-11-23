package com.metasoft.veyra.platform.communication.infrastructure.providers.stub;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.email.EmailGateway;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.resend.enabled", havingValue = "false")
public class DevEmailGatewayStub implements EmailGateway {

    @Override
    public void sendPlain(SendPlainEmailCommand command) {
        log.info("Resend integration disabled. Plain email not sent (recipientCount={}, subject={})",
                command.recipients().values().size(), command.subject());
    }

    @Override
    public void sendHtml(SendHtmlEmailCommand command) {
        log.info("Resend integration disabled. HTML email not sent (recipientCount={}, subject={})",
                command.recipients().values().size(), command.subject());
    }

    @Override
    public void sendTemplate(SendTemplateEmailCommand command) {
        log.info("Resend integration disabled. Template email not sent (recipientCount={}, templateId={})",
                command.recipients().values().size(), command.templateId());
    }
}
