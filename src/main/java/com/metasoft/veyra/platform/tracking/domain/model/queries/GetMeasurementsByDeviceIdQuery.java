package com.metasoft.veyra.platform.tracking.domain.model.queries;

public record GetMeasurementsByDeviceIdQuery(Long deviceId, int limit) {
    public GetMeasurementsByDeviceIdQuery(Long deviceId) {
        this(deviceId, 50);
    }
}
