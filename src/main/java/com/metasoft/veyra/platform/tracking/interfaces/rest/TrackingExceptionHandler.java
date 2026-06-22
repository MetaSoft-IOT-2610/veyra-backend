package com.metasoft.veyra.platform.tracking.interfaces.rest;

import com.metasoft.veyra.platform.tracking.domain.exceptions.DeviceAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.metasoft.veyra.platform.tracking.interfaces.rest")
public class TrackingExceptionHandler {

    @ExceptionHandler(DeviceAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDeviceAlreadyExists(DeviceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
