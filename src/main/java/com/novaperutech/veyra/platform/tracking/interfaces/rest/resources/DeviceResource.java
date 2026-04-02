package com.novaperutech.veyra.platform.tracking.interfaces.rest.resources;

public record DeviceResource(Long id,
                             String deviceId,
                             Long residentId,
                             String assignedBy,
                             String assignedAt,
                             String status) {
}
