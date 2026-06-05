package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

import java.util.List;

/**
 * REST resource representing the request body for updating an existing activity.
 * @param type the new activity type as a string
 * @param title the new descriptive title
 * @param isRecurring whether the activity should repeat
 * @param recurringDays the new list of recurring days as strings
 */
public record UpdateActivityResource(
        String type,
        String title,
        Boolean isRecurring,
        List<String> recurringDays
) {}
