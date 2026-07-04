package com.metasoft.veyra.platform.tracking.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
@Getter
public class MeasurementRecordedEvent  extends ApplicationEvent {
    private final  Long        deviceId;
    private final  LocalDateTime measurementTimestamp;
    private final  Integer       heartRate;
    private final  Double        temperature;
    private final  Integer       oxygenSaturation;
    private final  Boolean       clinicalRecord;


    public MeasurementRecordedEvent(Object source, Long deviceId, LocalDateTime timestamp, Integer heartRate, Double temperature, Integer oxygenSaturation, Boolean clinicalRecord) {
        super(source);
        this.deviceId = deviceId;
        this.measurementTimestamp = timestamp;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.oxygenSaturation = oxygenSaturation;
        this.clinicalRecord = clinicalRecord;

    }
}
