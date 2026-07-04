package com.metasoft.veyra.platform.health.interfaces.rest.resources;

public record VitalSignThresholdResource(
        Long id,
        Long residentId,
        Integer heartRateMin,
        Integer heartRateMax,
        Integer systolicMax,
        Integer diastolicMax,
        Double temperatureMin,
        Double temperatureMax,
        Integer oxygenSaturationMin,
        Integer oxygenSaturationMax,
        Integer respiratoryRateMin,
        Integer respiratoryRateMax
) {}
