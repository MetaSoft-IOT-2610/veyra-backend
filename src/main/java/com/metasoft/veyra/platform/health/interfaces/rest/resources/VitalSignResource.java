package com.metasoft.veyra.platform.health.interfaces.rest.resources;

import java.time.LocalDateTime;

public record VitalSignResource(
        Long id,
        Long residentId,
        String measurementId,
        Double temperature,
        Integer heartRate,
        Integer systolic,
        Integer diastolic,
        Integer oxygenSaturation,
        Integer respiratoryRate,
        LocalDateTime registeredAt,
        String severityLevel) {
}
