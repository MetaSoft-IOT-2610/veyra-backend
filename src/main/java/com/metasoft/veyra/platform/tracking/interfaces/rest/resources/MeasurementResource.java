package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import java.time.LocalDateTime;

public record MeasurementResource(String  id , Long deviceId, Double temperature, Integer heartRate, Integer oxygenSaturation,
                                  LocalDateTime timestamp) {
}
