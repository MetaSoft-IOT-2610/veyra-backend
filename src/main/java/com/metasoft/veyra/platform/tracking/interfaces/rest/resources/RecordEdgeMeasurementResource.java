package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import java.util.Map;

public record RecordEdgeMeasurementResource(
        String deviceId,
        String deviceType,
        String macAddress,
        String timestamp,
        Integer heartRate,
        Double temperature,
        Integer oxygenSaturation,
        Double ambientTemperature,
        Integer respiratoryRate,
        BloodPressurePayload bloodPressure,
        LocationPayload location,
        Integer satelliteCount,
        Integer satellitesInView,
        Map<String, Object> diagnostics,
        GatewayPayload gateway) {

    public record BloodPressurePayload(Integer systolic, Integer diastolic) {
    }

    public record LocationPayload(Double latitude, Double longitude) {
    }

    public record GatewayPayload(String deviceId, String deviceType) {
    }
}
