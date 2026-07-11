package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

public record RecordLocationResource(
        String deviceId,
        String macAddress,
        double latitude,
        double longitude) {
}