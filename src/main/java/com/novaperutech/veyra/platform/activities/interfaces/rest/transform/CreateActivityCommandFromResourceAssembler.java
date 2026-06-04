package com.novaperutech.veyra.platform.activities.interfaces.rest.transform;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.RecurringDay;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.CreateActivityResource;

import java.util.List;

public class CreateActivityCommandFromResourceAssembler {

    public static CreateActivityCommand toCommandFromResource(Long nursingHomeId, CreateActivityResource resource) {
        List<RecurringDay> days = resource.recurringDays() != null
                ? resource.recurringDays().stream().map(RecurringDay::valueOf).toList()
                : List.of();
        return new CreateActivityCommand(
                nursingHomeId,
                resource.residentId(),
                resource.healthcareStaffId(),
                ActivityType.valueOf(resource.type()),
                resource.title(),
                resource.isRecurring() != null && resource.isRecurring(),
                days
        );
    }
}
