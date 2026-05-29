package com.metasoft.veyra.platform.nursing.interfaces.rest;

import com.metasoft.veyra.platform.nursing.domain.model.queries.GetResidentsByRelativeIdQuery;
import com.metasoft.veyra.platform.nursing.domain.services.RelativeCommandService;

import com.metasoft.veyra.platform.nursing.domain.services.ResidentQueryServices;
import com.metasoft.veyra.platform.nursing.interfaces.rest.resources.AssignedRelativeForResidentResource;
import com.metasoft.veyra.platform.nursing.interfaces.rest.resources.ResidentResource;
import com.metasoft.veyra.platform.nursing.interfaces.rest.transform.AssignedResidentForRelativeFromResourceAssembler;
import com.metasoft.veyra.platform.nursing.interfaces.rest.transform.ResidentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api/v1/relatives/{relativeId}/residents",produces =APPLICATION_JSON_VALUE)
@RestController
@Tag(name = "Relatives")
public class RelativeResidentsController {
    private final RelativeCommandService relativeCommandService;
    private final ResidentQueryServices residentQueryServices;

    public RelativeResidentsController(RelativeCommandService relativeCommandService, ResidentQueryServices residentQueryServices) {
        this.relativeCommandService = relativeCommandService;
        this.residentQueryServices = residentQueryServices;
    }
    /**
     * GET /api/v1/relatives/{relativeId}/residents
     *
     * <p>Retrieve all residents linked to a given relative identifier.</p>
     *
     * <p>Note: Current implementation returns 404 Not Found when the query yields an empty result.
     * Consider returning 200 OK with an empty array instead if you want to differentiate "no association"
     * from "relative not found". Alternatively, validate the existence of the relative and return 404
     * only if the relative itself does not exist.</p>
     *
     * @param relativeId The identifier of the relative whose residents should be returned.
     * @return {@link ResponseEntity} containing a list of {@link ResidentResource} or an appropriate status code.
     */
    @GetMapping
    @Operation(
            summary = "Get residents by relative id",
            description = "Returns a list of residents associated with the given relative id. " +
                    "If no residents are found the endpoint currently returns 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Residents retrieved successfully. Returns a JSON array of ResidentResource objects."),
            @ApiResponse(responseCode = "404", description = "No residents found for the given relative id."),
            @ApiResponse(responseCode = "500", description = "Internal server error while processing the request.")
    })
    @Parameter(name = "relativeId", description = "The unique identifier of the relative (path parameter).", required = true)
    public ResponseEntity<List<ResidentResource>> getResidentsByRelativeId(@PathVariable Long relativeId) {
        var residents=residentQueryServices.handle(new GetResidentsByRelativeIdQuery(relativeId));
       if (residents.isEmpty()) {return ResponseEntity.notFound().build();}
      var residentsResource=residents.stream().map(ResidentResourceFromEntityAssembler::toResourceFromEntity).toList();
       return ResponseEntity.ok(residentsResource);
    }

    @PostMapping
    @Operation(summary = "Assign a resident to a relative", description = "Creates an association between a resident and a relative. ")
    @Parameter(name = "relativeId", description = "The unique identifier of the relative (path parameter).", required = true)
    @ApiResponses(value = {         @ApiResponse(responseCode = "201", description = "Resident successfully assigned to the relative."),
            @ApiResponse(responseCode = "400", description = "Invalid request data or association already exists."),
            @ApiResponse(responseCode = "404", description = "Relative or resident not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error while processing the request.")
    })
    public ResponseEntity<ResidentResource> assignResidentForRelative(
            @Valid @RequestBody AssignedRelativeForResidentResource resource,
            @PathVariable Long relativeId) {

        var command = AssignedResidentForRelativeFromResourceAssembler
                .toCommandFromResource(resource, relativeId);

        relativeCommandService.handle(command);

        var residents = residentQueryServices.handle(new GetResidentsByRelativeIdQuery(relativeId));

        if (residents.isEmpty()) { return ResponseEntity.notFound().build(); }

        var assigned = residents.stream()
                .filter(r -> r.getId().equals(resource.residentId()))
                .findFirst();

        if (assigned.isEmpty()) { return ResponseEntity.notFound().build(); }

        var residentResource = ResidentResourceFromEntityAssembler.toResourceFromEntity(assigned.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(residentResource);
    }
}
