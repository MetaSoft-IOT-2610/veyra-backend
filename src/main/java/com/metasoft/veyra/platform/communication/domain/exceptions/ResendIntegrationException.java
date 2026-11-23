package com.metasoft.veyra.platform.communication.domain.exceptions;

public class ResendIntegrationException extends CommunicationIntegrationException {
    public ResendIntegrationException(String message) {
        super(message);
    }

    public ResendIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
