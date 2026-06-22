package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceResource(
        @NotBlank(message = "External device id is required") String externalDeviceId,
        @NotBlank(message = "Device type is required") String deviceType,
        @NotBlank(message = "MacAddress is required") String macAddress) {
}
