package com.metasoft.veyra.platform.tracking.domain.model.commands;

public record UpdateDeviceCommand(
        Long id,
        String externalDeviceId,
        String deviceType,
        String macAddress) {
}
