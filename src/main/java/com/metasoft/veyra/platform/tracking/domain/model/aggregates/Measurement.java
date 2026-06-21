package com.metasoft.veyra.platform.tracking.domain.model.aggregates;
import com.metasoft.veyra.platform.shared.domain.model.aggregates.AuditableMongoAggregateRoot;
import com.metasoft.veyra.platform.tracking.domain.model.events.MeasurementRecordedEvent;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.*;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Document(collection = "measurements")
@Getter
public class Measurement extends AuditableMongoAggregateRoot<Measurement> {
    private final DeviceId deviceId;
    private final LocalDateTime timestamp;
    private final HeartRate heartRate;
    private final Temperature temperature;
    private final OxygenSaturation oxygenSaturation;
    public Measurement(Long deviceId, Integer heartRate, Double temperature, Integer oxygenSaturation) {
        this.deviceId = new DeviceId(deviceId);
        this.oxygenSaturation = new OxygenSaturation( oxygenSaturation);
        this.timestamp = LocalDateTime.now();
        this.heartRate = new HeartRate(heartRate);
        this.temperature = new Temperature(temperature);
     this.addDomainEvent(new MeasurementRecordedEvent(this,this.deviceId.deviceId(),this.timestamp,this.heartRate.heartRate(),this.temperature.temperature(),this.oxygenSaturation.oxygenSaturation()));
    }}