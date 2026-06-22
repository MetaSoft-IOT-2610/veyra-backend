package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import java.time.LocalDateTime;

public record MeasurementResource(
        String id,
        Long deviceId,
        Double temperature,
        Double ambientTemperature,
        Integer heartRate,
        Integer oxygenSaturation,
        LocalDateTime timestamp) {
}
