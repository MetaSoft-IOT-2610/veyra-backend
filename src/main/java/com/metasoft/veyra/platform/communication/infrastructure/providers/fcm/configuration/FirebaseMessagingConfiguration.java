package com.metasoft.veyra.platform.communication.infrastructure.providers.fcm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseMessagingConfiguration {

    @Value("${firebase.project.id:}")
    private String projectId;

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Bean
    FirebaseMessagingSettings firebaseMessagingSettings() {
        return new FirebaseMessagingSettings(projectId, credentialsPath);
    }
}
