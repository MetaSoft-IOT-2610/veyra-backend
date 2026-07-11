package com.metasoft.veyra.platform.tracking.domain.model.commands;

public record RecordLocationCommand(
        Long gatewayNursingHomeId,
        String deviceExternalId,
        String deviceMacAddress,
        double latitude,
        double longitude) {
}