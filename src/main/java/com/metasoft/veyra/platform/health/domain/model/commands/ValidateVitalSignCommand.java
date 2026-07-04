package com.metasoft.veyra.platform.health.domain.model.commands;

import java.time.LocalDateTime;

public record ValidateVitalSignCommand( String measurementId,
                                        Long deviceId,
                                        LocalDateTime registeredAt,
                                        Integer heartRate,
                                        Integer systolic,
                                        Integer diastolic,
                                        Double temperature,
                                        Integer oxygenSaturation,
                                        Integer respiratoryRate){
}
