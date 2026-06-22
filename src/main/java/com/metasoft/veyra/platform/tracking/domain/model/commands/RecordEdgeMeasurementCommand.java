package com.metasoft.veyra.platform.tracking.domain.model.commands;

public record RecordEdgeMeasurementCommand(
        Long gatewayNursingHomeId,
        String nodeExternalDeviceId,
        String nodeMacAddress,
        Integer heartRate,
        Double temperature,
        Double ambientTemperature,
        Integer oxygenSaturation,
        Double latitude,
        Double longitude) {
}
