package com.novaperutech.veyra.platform.activities.interfaces.rest.transform;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.ActivityResource;

public class ActivityResourceFromEntityAssembler {

    public static ActivityResource toResourceFromEntity(Activity activity) {
        var recurringDays = activity.getRecurringDays().stream()
                .map(Enum::name)
                .toList();
        return new ActivityResource(
                activity.getId(),
                activity.getResidentId(),
                activity.getHealthcareStaffId(),
                activity.getType().name(),
                activity.getTitle(),
                activity.getStatus().name(),
                activity.getIsRecurring(),
                recurringDays
        );
    }
}
