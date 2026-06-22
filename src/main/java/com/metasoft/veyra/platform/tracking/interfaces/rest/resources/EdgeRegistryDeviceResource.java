package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record EdgeRegistryDeviceResource(
        @JsonProperty("device_id") String deviceId,
        @JsonProperty("mac_address") String macAddress,
        @JsonProperty("device_type") String deviceType,
        @JsonProperty("status") String status,
        @JsonProperty("updated_at") String updatedAt) {
}
