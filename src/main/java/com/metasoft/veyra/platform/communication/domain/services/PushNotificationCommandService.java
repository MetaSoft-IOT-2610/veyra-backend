package com.metasoft.veyra.platform.communication.domain.services;

import com.metasoft.veyra.platform.communication.domain.model.commands.SendPushNotificationCommand;

public interface PushNotificationCommandService {
    void handle(SendPushNotificationCommand command);
}
