package com.metasoft.veyra.platform.tracking.application.internal.commandservices;
import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordMeasurementCommand;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementCommandService;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.mongodb.repositories.MeasurementRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MeasurementCommandServiceImpl implements MeasurementCommandService {
    private final MeasurementRepository measurementRepository;
   private final DeviceRepository deviceRepository;
    public MeasurementCommandServiceImpl(
            MeasurementRepository measurementRepository, DeviceRepository deviceRepository) {
        this.measurementRepository = measurementRepository;
        this.deviceRepository = deviceRepository;
    }
    @Override
    public Optional<Measurement> handle(RecordMeasurementCommand command) {
        if (!deviceRepository.existsDeviceById(command.deviceId())){
            throw  new IllegalArgumentException("device dont found");
        }
        var measurement = new Measurement(command.deviceId(),command.heartRate(),command.temperature(),command.oxygenSaturation());
        var save= measurementRepository.save(measurement);
        return Optional.of(save);
    }
}