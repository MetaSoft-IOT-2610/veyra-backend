package com.metasoft.veyra.platform.health.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EdgeVitalSignThresholdResource(
        @JsonProperty("device_id") String deviceId,
        @JsonProperty("heart_rate_min") Integer heartRateMin,
        @JsonProperty("heart_rate_max") Integer heartRateMax,
        @JsonProperty("systolic_min") Integer systolicMin,
        @JsonProperty("systolic_max") Integer systolicMax,
        @JsonProperty("diastolic_min") Integer diastolicMin,
        @JsonProperty("diastolic_max") Integer diastolicMax,
        @JsonProperty("temperature_min") Double temperatureMin,
        @JsonProperty("temperature_max") Double temperatureMax,
        @JsonProperty("oxygen_saturation_min") Integer oxygenSaturationMin,
        @JsonProperty("oxygen_saturation_max") Integer oxygenSaturationMax,
        @JsonProperty("respiratory_rate_min") Integer respiratoryRateMin,
        @JsonProperty("respiratory_rate_max") Integer respiratoryRateMax,
        @JsonProperty("updated_at") String updatedAt
) {
}
