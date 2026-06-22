package com.metasoft.veyra.platform.tracking.domain.model.commands;

import java.time.Instant;

public record RecordEdgeMeasurementCommand(
        Long gatewayNursingHomeId,
        String nodeExternalDeviceId,
        String nodeMacAddress,
        Instant recordedAt,
        Integer heartRate,
        Double temperature,
        Double ambientTemperature,
        Integer oxygenSaturation,
        Double latitude,
        Double longitude) {
}
