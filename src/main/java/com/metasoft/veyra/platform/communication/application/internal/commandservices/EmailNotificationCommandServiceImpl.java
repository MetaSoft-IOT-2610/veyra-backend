package com.metasoft.veyra.platform.communication.application.internal.commandservices;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.email.EmailGateway;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendHtmlEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPlainEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendRenderedTemplateEmailCommand;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendTemplateEmailCommand;
import com.metasoft.veyra.platform.communication.domain.services.EmailNotificationCommandService;
import com.metasoft.veyra.platform.shared.infrastructure.templates.EmailTemplateRenderer;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationCommandServiceImpl implements EmailNotificationCommandService {

    private final EmailGateway emailGateway;
    private final EmailTemplateRenderer emailTemplateRenderer;

    public EmailNotificationCommandServiceImpl(EmailGateway emailGateway, EmailTemplateRenderer emailTemplateRenderer) {
        this.emailGateway = emailGateway;
        this.emailTemplateRenderer = emailTemplateRenderer;
    }

    @Override
    public void handle(SendPlainEmailCommand command) {
        emailGateway.sendPlain(command);
    }

    @Override
    public void handle(SendHtmlEmailCommand command) {
        emailGateway.sendHtml(command);
    }

    @Override
    public void handle(SendTemplateEmailCommand command) {
        emailGateway.sendTemplate(command);
    }

    @Override
    public void handle(SendRenderedTemplateEmailCommand command) {
        String htmlContent = emailTemplateRenderer.render(command.template(), command.variables());
        emailGateway.sendHtml(new SendHtmlEmailCommand(
                command.recipients(),
                command.subject(),
                htmlContent,
                null
        ));
    }
}
