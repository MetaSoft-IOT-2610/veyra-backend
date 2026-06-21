package com.metasoft.veyra.platform.tracking.domain.model.valueobjects;


public record HeartRate(Integer heartRate) {

    public HeartRate {
        if (heartRate == null || heartRate < 0 || heartRate > 300) {
            throw new IllegalArgumentException("Heart rate must be between 0 and 300 bpm");
        }
    }
}