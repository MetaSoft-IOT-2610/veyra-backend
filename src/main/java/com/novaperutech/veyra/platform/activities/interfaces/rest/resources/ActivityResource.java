package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

import java.util.List;

public record ActivityResource(
        Long id,
        Long nursingHomeId,
        Long residentId,
        Long healthcareStaffId,
        String type,
        String title,
        String status,
        Boolean isRecurring,
        List<String> recurringDays
) {}
