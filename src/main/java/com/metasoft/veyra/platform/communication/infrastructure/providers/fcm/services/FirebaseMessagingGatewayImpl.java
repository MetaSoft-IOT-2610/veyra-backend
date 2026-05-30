package com.metasoft.veyra.platform.communication.infrastructure.providers.fcm.services;

import com.metasoft.veyra.platform.communication.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.metasoft.veyra.platform.communication.domain.exceptions.CommunicationProviderNotConfiguredException;
import com.metasoft.veyra.platform.communication.domain.model.commands.SendPushNotificationCommand;
import com.metasoft.veyra.platform.communication.infrastructure.providers.fcm.configuration.FirebaseMessagingSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.fcm.enabled", havingValue = "true")
public class FirebaseMessagingGatewayImpl implements FirebaseMessagingGateway {

    private final FirebaseMessagingSettings firebaseMessagingSettings;

    public FirebaseMessagingGatewayImpl(FirebaseMessagingSettings firebaseMessagingSettings) {
        this.firebaseMessagingSettings = firebaseMessagingSettings;
    }

    @Override
    public void send(SendPushNotificationCommand command) {
        if (firebaseMessagingSettings.projectId() == null || firebaseMessagingSettings.projectId().isBlank()) {
            throw new CommunicationProviderNotConfiguredException("Firebase Cloud Messaging");
        }

        log.info("FCM integration is configured. Push sending is currently disabled (deviceToken={}, title={})",
                command.deviceToken(), command.title());
    }
}
