package com.novaperutech.veyra.platform.health.interfaces.rest.resources;

public record VitalSignResource(Long id,Long residentId,String measurementId,String severityLevel) {
}
