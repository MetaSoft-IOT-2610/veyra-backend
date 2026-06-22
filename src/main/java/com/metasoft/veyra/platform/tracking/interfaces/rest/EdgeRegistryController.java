package com.metasoft.veyra.platform.tracking.interfaces.rest;

import com.metasoft.veyra.platform.tracking.domain.model.queries.GetEdgeRegistryDeltaQuery;
import com.metasoft.veyra.platform.tracking.domain.services.DeviceQueryService;
import com.metasoft.veyra.platform.tracking.infrastructure.authorization.EdgeGatewayAuthentication;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.EdgeRegistryResponse;
import com.metasoft.veyra.platform.tracking.interfaces.rest.transform.EdgeRegistryDeviceResourceFromEntityAssembler;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/edge/registry", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Edge Registry", description = "Device registry mirror for on-premise edge gateways")
public class EdgeRegistryController {

    private final DeviceQueryService deviceQueryService;

    public EdgeRegistryController(DeviceQueryService deviceQueryService) {
        this.deviceQueryService = deviceQueryService;
    }

    @GetMapping
    @Operation(summary = "Pull IoT node registry delta for the authenticated edge gateway")
    public ResponseEntity<EdgeRegistryResponse> getRegistry(
            Authentication authentication,
            @RequestParam(required = false) String since) {

        if (!(authentication instanceof EdgeGatewayAuthentication edgeAuthentication)) {
            return ResponseEntity.status(401).build();
        }

        LocalDateTime sinceTime = parseSince(since);
        var nursingHomeId = edgeAuthentication.getPrincipal().nursingHomeId();
        var devices = deviceQueryService.handle(new GetEdgeRegistryDeltaQuery(nursingHomeId, sinceTime));
        var resources = devices.stream()
                .filter(device -> device.getExternalDeviceId() != null && !device.getExternalDeviceId().isBlank())
                .map(EdgeRegistryDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new EdgeRegistryResponse(resources));
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
