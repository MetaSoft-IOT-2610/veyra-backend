package com.metasoft.veyra.platform.tracking.domain.model.valueobjects;



public record OxygenSaturation(Integer oxygenSaturation) {

    public OxygenSaturation {
        if (oxygenSaturation == null || oxygenSaturation < 0 || oxygenSaturation > 100) {
            throw new IllegalArgumentException("Oxygen saturation must be between 0 and 100%");
        }
    }
}