package com.metasoft.veyra.platform.communication.application.internal.commandservices;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.sendgrid.SendGridGateway;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendEmailCommand;
import com.metasoft.veyra.platform.communication.domain.services.EmailNotificationCommandService;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationCommandServiceImpl implements EmailNotificationCommandService {

    private final SendGridGateway sendGridGateway;

    public EmailNotificationCommandServiceImpl(SendGridGateway sendGridGateway) {
        this.sendGridGateway = sendGridGateway;
    }

    @Override
    public void handle(SendEmailCommand command) {
        sendGridGateway.send(command);
    }
}
