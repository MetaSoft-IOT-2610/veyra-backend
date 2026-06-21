package com.metasoft.veyra.platform.tracking.interfaces.rest.transform;

import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordMeasurementCommand;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.RecordMeasurementResource;

public class RecordMeasurementCommandFromEntityAssembler {
    public static RecordMeasurementCommand toCommandFromResource(RecordMeasurementResource resource){
        return new RecordMeasurementCommand(resource.deviceId(),resource.temperature(),resource.heartRate(),resource.oxygenSaturation());
    }
}
