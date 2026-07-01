package com.metasoft.veyra.platform.health.interfaces.rest;

import com.metasoft.veyra.platform.health.domain.model.aggregates.VitalSignThreshold;
import com.metasoft.veyra.platform.health.domain.model.valueobjects.ResidentId;
import com.metasoft.veyra.platform.health.infrastructure.persistence.jpa.repositories.VitalSignThresholdRepository;
import com.metasoft.veyra.platform.health.interfaces.rest.resources.EdgeVitalSignThresholdResource;
import com.metasoft.veyra.platform.health.interfaces.rest.resources.EdgeVitalSignThresholdsResponse;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.AssignmentStatus;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceType;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.NursingHomeId;
import com.metasoft.veyra.platform.tracking.infrastructure.authorization.EdgeGatewayAuthentication;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/edge/thresholds", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Edge Thresholds", description = "Vital-sign threshold mirror for on-premise edge gateways")
public class EdgeVitalSignThresholdsController {

    private final DeviceRepository deviceRepository;
    private final VitalSignThresholdRepository thresholdRepository;

    public EdgeVitalSignThresholdsController(
            DeviceRepository deviceRepository,
            VitalSignThresholdRepository thresholdRepository) {
        this.deviceRepository = deviceRepository;
        this.thresholdRepository = thresholdRepository;
    }

    @GetMapping
    @Operation(summary = "Pull vital-sign threshold delta for the authenticated edge gateway")
    public ResponseEntity<EdgeVitalSignThresholdsResponse> getThresholds(
            Authentication authentication,
            @RequestParam(required = false) String since) {

        if (!(authentication instanceof EdgeGatewayAuthentication edgeAuthentication)) {
            return ResponseEntity.status(401).build();
        }

        var sinceTime = parseSince(since);
        var nursingHomeId = new NursingHomeId(edgeAuthentication.getPrincipal().nursingHomeId());
        var thresholds = deviceRepository
                .findByNursingHomeIdAndDeviceTypeNot(nursingHomeId, DeviceType.EDGE_GATEWAY)
                .stream()
                .filter(device -> device.getStatus() == AssignmentStatus.ASSIGNED)
                .filter(device -> device.getResidentId() != null)
                .filter(device -> device.getExternalDeviceId() != null && !device.getExternalDeviceId().isBlank())
                .map(device -> thresholdRepository
                        .findByResidentId(new ResidentId(device.getResidentId().residentId()))
                        .filter(threshold -> sinceTime == null || threshold.getUpdatedAt().isAfter(sinceTime))
                        .map(threshold -> toResource(device.getExternalDeviceId(), threshold))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(new EdgeVitalSignThresholdsResponse(thresholds));
    }

    private static EdgeVitalSignThresholdResource toResource(
            String externalDeviceId,
            VitalSignThreshold threshold) {
        return new EdgeVitalSignThresholdResource(
                externalDeviceId,
                threshold.getHeartRateMin(),
                threshold.getHeartRateMax(),
                null,
                threshold.getSystolicMax(),
                null,
                threshold.getDiastolicMax(),
                threshold.getTemperatureMin(),
                threshold.getTemperatureMax(),
                threshold.getOxygenSaturationMin(),
                null,
                threshold.getRespiratoryRateMin(),
                threshold.getRespiratoryRateMax(),
                threshold.getUpdatedAt().toString()
        );
    }

    private static LocalDateTime parseSince(String since) {
        if (since == null || since.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(since).toLocalDateTime();
        } catch (DateTimeParseException ex) {
            return LocalDateTime.parse(since);
        }
    }
}
