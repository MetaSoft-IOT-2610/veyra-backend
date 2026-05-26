package com.novaperutech.veyra.platform.activities.interfaces.rest.transform;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.CreateActivityResource;

public class CreateActivityCommandFromResourceAssembler {

    public static CreateActivityCommand toCommandFromResource(CreateActivityResource resource) {
        return new CreateActivityCommand(
                resource.residentId(),
                resource.healthcareStaffId(),
                ActivityType.valueOf(resource.type())
        );
    }
}
