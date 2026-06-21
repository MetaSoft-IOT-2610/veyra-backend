package com.metasoft.veyra.platform.tracking.interfaces.rest;

import com.metasoft.veyra.platform.tracking.domain.model.queries.GetAllMeasurementQuery;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementCommandService;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementQueryService;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.MeasurementResource;
import com.metasoft.veyra.platform.tracking.interfaces.rest.resources.RecordMeasurementResource;
import com.metasoft.veyra.platform.tracking.interfaces.rest.transform.MeasurementResourceFromEntityAssembler;
import com.metasoft.veyra.platform.tracking.interfaces.rest.transform.RecordMeasurementCommandFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/measurements", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Measurements", description = "Endpoints for managing measurements")
public class MeasurementsController {

    private final MeasurementQueryService measurementQueryService;
    private final MeasurementCommandService measurementCommandService;

    public MeasurementsController(MeasurementQueryService measurementQueryService,
                                  MeasurementCommandService measurementCommandService) {
        this.measurementQueryService = measurementQueryService;
        this.measurementCommandService = measurementCommandService;
    }

    @PostMapping
    @Operation(summary = "Record a vital sign measurement for a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "record measurement"),
            @ApiResponse(responseCode = "400", description = "bad request")
    })
    public ResponseEntity<MeasurementResource> recordMeasurement(@Valid @RequestBody RecordMeasurementResource resource) {
        var command = RecordMeasurementCommandFromEntityAssembler.toCommandFromResource(resource);
        var measurement = measurementCommandService.handle(command);
        if (measurement.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var measurementEntity = measurement.get();
        var measurementResource = MeasurementResourceFromEntityAssembler.toResourceFromEntity(measurementEntity);
        return new ResponseEntity<>(measurementResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all measurements", description = "Retrieve a list of all measurements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "not found ")
    })
    public ResponseEntity<List<MeasurementResource>> getAllMeasurements() {
        var query = measurementQueryService.handle(new GetAllMeasurementQuery());
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var resource = query.stream().map(MeasurementResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(resource);
    }
}