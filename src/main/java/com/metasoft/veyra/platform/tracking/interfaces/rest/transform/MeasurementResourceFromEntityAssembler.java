package com.metasoft.veyra.platform.tracking.interfaces.rest.transform;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.MeasurementResource;

import java.time.ZoneOffset;

public class MeasurementResourceFromEntityAssembler {
    public static MeasurementResource toResourceFromEntity(Measurement measurement){
        return new MeasurementResource(
                measurement.getId(),
                measurement.getDeviceId().deviceId(),
                measurement.getTemperature(),
                measurement.getAmbientTemperature(),
                measurement.getHeartRate(),
                measurement.getOxygenSaturation(),
                measurement.getTimestamp().atZone(ZoneOffset.UTC).toInstant()
        );
    }
}
