package com.novaperutech.veyra.platform.activities.interfaces.rest.transform;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.ActivityResource;

public class ActivityResourceFromEntityAssembler {

    public static ActivityResource toResourceFromEntity(Activity activity) {
        return new ActivityResource(
                activity.getId(),
                activity.getResidentId(),
                activity.getHealthcareStaffId(),
                activity.getType().name(),
                activity.getStatus().name()
        );
    }
}
