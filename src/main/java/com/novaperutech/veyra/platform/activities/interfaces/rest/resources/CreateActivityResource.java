package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

public record CreateActivityResource(
        Long residentId,
        Long healthcareStaffId,
        String type,
        String status
) {}
