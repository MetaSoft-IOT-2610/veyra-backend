package com.metasoft.veyra.platform.tracking.domain.model.aggregates;

import com.metasoft.veyra.platform.shared.domain.model.aggregates.AuditableMongoAggregateRoot;
import com.metasoft.veyra.platform.tracking.domain.model.events.MeasurementRecordedEvent;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceId;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.HeartRate;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.OxygenSaturation;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.Temperature;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Document(collection = "measurements")
@Getter
public class Measurement extends AuditableMongoAggregateRoot<Measurement> {
    private DeviceId deviceId;
    private LocalDateTime timestamp;
    private Integer heartRate;
    private Double temperature;
    private Double ambientTemperature;
    private Integer oxygenSaturation;

    protected Measurement() {
    }

    @PersistenceCreator
    Measurement(
            DeviceId deviceId,
            LocalDateTime timestamp,
            Integer heartRate,
            Double temperature,
            Double ambientTemperature,
            Integer oxygenSaturation) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.ambientTemperature = ambientTemperature;
        this.oxygenSaturation = oxygenSaturation;
    }

    public Measurement(
            Long deviceId,
            Integer heartRate,
            Double temperature,
            Integer oxygenSaturation) {
        this(deviceId, heartRate, temperature, null, oxygenSaturation);
    }

    public static Measurement fromEdgeReading(
            Long deviceId,
            Instant recordedAt,
            Integer heartRate,
            Double bodyTemperature,
            Double ambientTemperature,
            Integer oxygenSaturation) {

        var validatedHeartRate = validateHeartRate(heartRate);
        var validatedBodyTemperature = validateBodyTemperature(bodyTemperature);
        var validatedAmbientTemperature = validateAmbientTemperature(ambientTemperature);
        var validatedOxygenSaturation = validateOxygenSaturation(oxygenSaturation);

        if (validatedHeartRate == null
                && validatedBodyTemperature == null
                && validatedAmbientTemperature == null
                && validatedOxygenSaturation == null) {
            throw new IllegalArgumentException("At least one vital sign is required");
        }

        return new Measurement(
                deviceId,
                recordedAt,
                validatedHeartRate,
                validatedBodyTemperature,
                validatedAmbientTemperature,
                validatedOxygenSaturation);
    }

    private Measurement(
            Long deviceId,
            Instant recordedAt,
            Integer heartRate,
            Double bodyTemperature,
            Double ambientTemperature,
            Integer oxygenSaturation) {
        this.deviceId = new DeviceId(deviceId);
        this.timestamp = LocalDateTime.ofInstant(
                recordedAt != null ? recordedAt : Instant.now(),
                ZoneOffset.UTC);
        this.heartRate = heartRate;
        this.temperature = bodyTemperature;
        this.ambientTemperature = ambientTemperature;
        this.oxygenSaturation = oxygenSaturation;
        this.addDomainEvent(new MeasurementRecordedEvent(
                this,
                this.deviceId.deviceId(),
                this.timestamp,
                this.heartRate,
                this.temperature != null ? this.temperature : this.ambientTemperature,
                this.oxygenSaturation));
    }

    private static Integer validateHeartRate(Integer value) {
        if (value == null) {
            return null;
        }
        return new HeartRate(value).heartRate();
    }

    private static Double validateBodyTemperature(Double value) {
        if (value == null) {
            return null;
        }
        return new Temperature(value).temperature();
    }

    private static Double validateAmbientTemperature(Double value) {
        if (value == null) {
            return null;
        }
        if (value < -40.0 || value > 60.0) {
            throw new IllegalArgumentException("Ambient temperature must be between -40.0 and 60.0 °C");
        }
        return value;
    }

    private static Integer validateOxygenSaturation(Integer value) {
        if (value == null) {
            return null;
        }
        return new OxygenSaturation(value).oxygenSaturation();
    }
}
