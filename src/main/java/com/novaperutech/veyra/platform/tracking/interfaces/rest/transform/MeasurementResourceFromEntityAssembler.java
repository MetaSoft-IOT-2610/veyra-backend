package com.novaperutech.veyra.platform.tracking.interfaces.rest.transform;

import com.novaperutech.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.novaperutech.veyra.platform.tracking.interfaces.rest.resources.MeasurementResource;

public class MeasurementResourceFromEntityAssembler {
    public static MeasurementResource toResourceFromEntity(Measurement measurement){
        return new MeasurementResource(
                measurement.getId(),
                measurement.getDeviceId().deviceId()
        );
    }
}
