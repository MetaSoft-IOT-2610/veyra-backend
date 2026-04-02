package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.AssignRoomForResidentCommand;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AssignedRoomForResidentResource;

public class AssignedRoomForResidentCommandFromResourceAssembler {
    public static AssignRoomForResidentCommand toCommandFromResource(Long nursingHomeId, Long residentId, AssignedRoomForResidentResource resource){
        return new AssignRoomForResidentCommand(
                nursingHomeId,
                resource.roomNumber(),
                residentId
        );
    }
}
