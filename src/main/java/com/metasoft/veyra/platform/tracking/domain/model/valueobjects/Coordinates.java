package com.metasoft.veyra.platform.tracking.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinates(double latitude, double longitude) {
    public Coordinates {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }
    }
}