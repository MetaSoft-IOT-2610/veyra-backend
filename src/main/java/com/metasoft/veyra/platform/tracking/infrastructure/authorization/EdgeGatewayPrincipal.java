package com.metasoft.veyra.platform.tracking.infrastructure.authorization;

/**
 * Authenticated on-premise edge gateway identity resolved from HTTP headers.
 */
public record EdgeGatewayPrincipal(
        Long deviceId,
        Long nursingHomeId,
        String externalDeviceId,
        String macAddress) {
}
