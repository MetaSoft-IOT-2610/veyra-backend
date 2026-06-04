package com.novaperutech.veyra.platform.activities.domain.model.commands;

import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.RecurringDay;

import java.util.List;

public record UpdateActivityCommand(
        Long activityId,
        ActivityType type,
        String title,
        Boolean isRecurring,
        List<RecurringDay> recurringDays
) {}
