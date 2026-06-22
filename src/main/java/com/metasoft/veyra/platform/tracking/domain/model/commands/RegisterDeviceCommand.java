package com.metasoft.veyra.platform.tracking.domain.model.commands;

public record RegisterDeviceCommand(
        Long nursingHomeId,
        String externalDeviceId,
        String deviceType,
        String macAddress) {
}
