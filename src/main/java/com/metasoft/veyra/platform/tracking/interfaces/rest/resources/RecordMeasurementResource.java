package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

public record RecordMeasurementResource(Long deviceId,Double temperature,Integer heartRate,Integer oxygenSaturation) {
}
