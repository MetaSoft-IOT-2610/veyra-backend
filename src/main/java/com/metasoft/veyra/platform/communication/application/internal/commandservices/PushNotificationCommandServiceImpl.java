package com.metasoft.veyra.platform.communication.application.internal.commandservices;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPushNotificationCommand;
import com.metasoft.veyra.platform.communication.domain.services.PushNotificationCommandService;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationCommandServiceImpl implements PushNotificationCommandService {

    private final FirebaseMessagingGateway firebaseMessagingGateway;

    public PushNotificationCommandServiceImpl(FirebaseMessagingGateway firebaseMessagingGateway) {
        this.firebaseMessagingGateway = firebaseMessagingGateway;
    }

    @Override
    public void handle(SendPushNotificationCommand command) {
        firebaseMessagingGateway.send(command);
    }
}
