package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

public record ActivityResource(
        Long id,
        Long residentId,
        Long healthcareStaffId,
        String type,
        String status
) {}
