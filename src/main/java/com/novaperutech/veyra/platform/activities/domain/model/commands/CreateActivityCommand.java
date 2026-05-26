package com.novaperutech.veyra.platform.activities.domain.model.commands;

import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;

public record CreateActivityCommand(
        Long residentId,
        Long healthcareStaffId,
        ActivityType type
) {}
