package com.metasoft.veyra.platform.tracking.interfaces.rest.transform;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Device;
import com.metasoft.veyra.platform.tracking.infrastructure.authorization.MacAddressNormalizer;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.EdgeRegistryDeviceResource;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.RecordEdgeMeasurementResource;

import java.time.format.DateTimeFormatter;

public class EdgeRegistryDeviceResourceFromEntityAssembler {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private EdgeRegistryDeviceResourceFromEntityAssembler() {
    }

    public static EdgeRegistryDeviceResource toResourceFromEntity(Device entity) {
        var updatedAt = entity.getUpdatedAt() != null
                ? entity.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC).format(ISO_FORMATTER)
                : null;
        return new EdgeRegistryDeviceResource(
                entity.getExternalDeviceId(),
                entity.getMacAddress().macAddress(),
                entity.getDeviceType().name(),
                entity.getIotStatus().name(),
                updatedAt
        );
    }

    public static com.metasoft.veyra.platform.tracking.domain.model.commands.RecordEdgeMeasurementCommand toCommandFromResource(
            RecordEdgeMeasurementResource resource,
            Long gatewayNursingHomeId) {

        if (resource.deviceId() == null || resource.deviceId().isBlank()) {
            throw new IllegalArgumentException("deviceId is required");
        }
        if (resource.macAddress() == null || resource.macAddress().isBlank()) {
            throw new IllegalArgumentException("macAddress is required");
        }

        Double latitude = null;
        Double longitude = null;
        if (resource.location() != null) {
            latitude = resource.location().latitude();
            longitude = resource.location().longitude();
        }

        Double bodyTemperature = resource.temperature();
        if (bodyTemperature != null && (bodyTemperature < 30.0 || bodyTemperature > 45.0)) {
            bodyTemperature = null;
        }

        return new com.metasoft.veyra.platform.tracking.domain.model.commands.RecordEdgeMeasurementCommand(
                gatewayNursingHomeId,
                resource.deviceId().trim(),
                MacAddressNormalizer.normalize(resource.macAddress()),
                resource.heartRate(),
                bodyTemperature,
                resource.ambientTemperature(),
                resource.oxygenSaturation(),
                latitude,
                longitude
        );
    }
}
