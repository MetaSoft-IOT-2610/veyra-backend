package com.novaperutech.veyra.platform.activities.interfaces.rest.transform;

import com.novaperutech.veyra.platform.activities.domain.model.commands.UpdateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.RecurringDay;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.UpdateActivityResource;

import java.util.List;

public class UpdateActivityCommandFromResourceAssembler {

    public static UpdateActivityCommand toCommandFromResource(Long activityId, UpdateActivityResource resource) {
        List<RecurringDay> days = resource.recurringDays() != null
                ? resource.recurringDays().stream().map(RecurringDay::valueOf).toList()
                : List.of();
        return new UpdateActivityCommand(
                activityId,
                ActivityType.valueOf(resource.type()),
                resource.title(),
                resource.isRecurring() != null && resource.isRecurring(),
                days
        );
    }
}
