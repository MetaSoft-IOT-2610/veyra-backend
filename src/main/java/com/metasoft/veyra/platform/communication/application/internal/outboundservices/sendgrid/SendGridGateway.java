package com.metasoft.veyra.platform.communication.application.internal.outboundservices.sendgrid;

import com.metasoft.veyra.platform.communication.domain.model.commands.SendEmailCommand;

public interface SendGridGateway {
    void send(SendEmailCommand command);
}
