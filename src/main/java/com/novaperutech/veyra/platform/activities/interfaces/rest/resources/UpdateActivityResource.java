package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

import java.util.List;

public record UpdateActivityResource(
        String type,
        String title,
        Boolean isRecurring,
        List<String> recurringDays
) {}
