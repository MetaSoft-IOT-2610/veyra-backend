package com.metasoft.veyra.platform.tracking.application.internal.commandservices;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordEdgeMeasurementCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordLocationCommand;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceType;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.IotStatus;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.MacAddress;
import com.metasoft.veyra.platform.tracking.domain.services.LocationCommandService;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementCommandService;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.mongodb.repositories.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeasurementCommandServiceImpl implements MeasurementCommandService {

    private final MeasurementRepository measurementRepository;
    private final DeviceRepository deviceRepository;
    private final LocationCommandService locationCommandService;

    public MeasurementCommandServiceImpl(
            MeasurementRepository measurementRepository,
            DeviceRepository deviceRepository,
            LocationCommandService locationCommandService) {
        this.measurementRepository = measurementRepository;
        this.deviceRepository = deviceRepository;
        this.locationCommandService = locationCommandService;
    }

    @Override
    public Optional<Measurement> handle(RecordEdgeMeasurementCommand command) {
        var macAddress = new MacAddress(command.nodeMacAddress());
        var node = deviceRepository.findByExternalDeviceIdAndMacAddress(
                        command.nodeExternalDeviceId(),
                        macAddress)
                .orElseThrow(() -> new IllegalArgumentException(
                        "IoT node not found for deviceId and macAddress"));

        if (node.getDeviceType() == DeviceType.EDGE_GATEWAY) {
            throw new IllegalArgumentException("Edge gateway cannot publish node telemetry");
        }
        if (node.getIotStatus() != IotStatus.ACTIVE) {
            throw new IllegalStateException("IoT node access is revoked");
        }
        if (!node.getNursingHomeId().nursingHomeId().equals(command.gatewayNursingHomeId())) {
            throw new IllegalArgumentException("IoT node does not belong to the authenticated gateway nursing home");
        }

        var measurement = Measurement.fromEdgeReading(
                node.getId(),
                command.heartRate(),
                command.temperature(),
                command.ambientTemperature(),
                command.oxygenSaturation());
        var saved = measurementRepository.save(measurement);

        if (command.latitude() != null && command.longitude() != null) {
            locationCommandService.handle(new RecordLocationCommand(
                    node.getId(),
                    command.latitude(),
                    command.longitude()
            ));
        }

        return Optional.of(saved);
    }
}
