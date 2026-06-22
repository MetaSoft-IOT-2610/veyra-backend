package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import java.time.LocalDateTime;

public record DeviceResource(
        Long id,
        Long nursingHomeId,
        String externalDeviceId,
        String deviceType,
        String status,
        String iotStatus,
        Long residentId,
        LocalDateTime assignedAt,
        String macAddress) {
}
