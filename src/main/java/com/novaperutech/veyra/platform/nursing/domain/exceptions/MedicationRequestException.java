package com.novaperutech.veyra.platform.nursing.domain.exceptions;

public class MedicationRequestException extends RuntimeException {
    public MedicationRequestException(String message) {
        super(message);
    }
}
