package com.metasoft.veyra.platform.tracking.domain.model.valueobjects;



public record Temperature(Double temperature) {

    public Temperature {
        if (temperature == null || temperature < 30.0 || temperature > 45.0) {
            throw new IllegalArgumentException("Temperature must be between 30.0 and 45.0 °C");
        }
    }
}