package com.metasoft.veyra.platform.tracking.interfaces.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementCommandService;
import com.metasoft.veyra.platform.tracking.infrastructure.authorization.EdgeGatewayAuthentication;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.MeasurementResource;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.RecordEdgeMeasurementResource;
import com.metasoft.veyra.platform.tracking.interfaces.rest.transform.EdgeRegistryDeviceResourceFromEntityAssembler;
import com.metasoft.veyra.platform.tracking.interfaces.rest.transform.MeasurementResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/measurements", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Measurements", description = "Edge gateway telemetry ingestion")
public class MeasurementsController {

    private final MeasurementCommandService measurementCommandService;
    private final ObjectMapper objectMapper;

    public MeasurementsController(
            MeasurementCommandService measurementCommandService,
            ObjectMapper objectMapper) {
        this.measurementCommandService = measurementCommandService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Record a vital sign measurement from an edge gateway")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "record measurement"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "401", description = "unauthorized"),
            @ApiResponse(responseCode = "403", description = "forbidden"),
            @ApiResponse(responseCode = "404", description = "device not found")
    })
    public ResponseEntity<MeasurementResource> recordMeasurement(
            @RequestBody JsonNode body) throws com.fasterxml.jackson.core.JsonProcessingException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof EdgeGatewayAuthentication edgeAuthentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var resource = objectMapper.treeToValue(body, RecordEdgeMeasurementResource.class);
        var command = EdgeRegistryDeviceResourceFromEntityAssembler.toCommandFromResource(
                resource,
                edgeAuthentication.getPrincipal().nursingHomeId()
        );
        try {
            var measurement = measurementCommandService.handle(command);
            if (measurement.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            return new ResponseEntity<>(
                    MeasurementResourceFromEntityAssembler.toResourceFromEntity(measurement.get()),
                    HttpStatus.CREATED
            );
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
