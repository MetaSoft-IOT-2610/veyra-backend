package com.metasoft.veyra.platform.tracking.interfaces.rest.transform;

import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordLocationCommand;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.RecordLocationResource;

public class RecordLocationCommandFromResourceAssembler {
    public static RecordLocationCommand toCommandFromResource(
            RecordLocationResource resource,
            Long gatewayNursingHomeId) {
        return new RecordLocationCommand(
                gatewayNursingHomeId,
                resource.deviceId(),
                resource.macAddress(),
                resource.latitude(),
                resource.longitude());
    }
}