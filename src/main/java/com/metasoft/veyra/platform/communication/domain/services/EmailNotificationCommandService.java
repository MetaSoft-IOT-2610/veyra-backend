package com.metasoft.veyra.platform.communication.domain.services;

import com.metasoft.veyra.platform.communication.domain.model.commands.SendEmailCommand;

public interface EmailNotificationCommandService {
    void handle(SendEmailCommand command);
}
