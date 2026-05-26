package com.novaperutech.veyra.platform.activities.interfaces.rest;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByResidentIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivityByIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetAllActivitiesQuery;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityCommandService;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityQueryService;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.ActivityResource;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.CreateActivityResource;
import com.novaperutech.veyra.platform.activities.interfaces.rest.transform.ActivityResourceFromEntityAssembler;
import com.novaperutech.veyra.platform.activities.interfaces.rest.transform.CreateActivityCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/activities", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Activities", description = "Activities Management Endpoints")
public class ActivitiesController {

    private final ActivityCommandService activityCommandService;
    private final ActivityQueryService activityQueryService;

    public ActivitiesController(ActivityCommandService activityCommandService, ActivityQueryService activityQueryService) {
        this.activityCommandService = activityCommandService;
        this.activityQueryService = activityQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Get activities",
            description = "Returns all activities. Filter by residentId using the optional query param."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully")
    })
    @Parameter(name = "residentId", description = "Optional filter by resident ID")
    public ResponseEntity<List<ActivityResource>> getActivities(
            @RequestParam(required = false) Long residentId) {

        var activities = residentId != null
                ? activityQueryService.handle(new GetActivitiesByResidentIdQuery(residentId))
                : activityQueryService.handle(new GetAllActivitiesQuery());

        var resources = activities.stream()
                .map(ActivityResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @PostMapping
    @Operation(summary = "Create activity", description = "Registers a new activity with status IN_PROGRESS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<ActivityResource> createActivity(@RequestBody CreateActivityResource resource) {
        var command = CreateActivityCommandFromResourceAssembler.toCommandFromResource(resource);
        var activityId = activityCommandService.handle(command);

        if (activityId == 0L) {
            return ResponseEntity.badRequest().build();
        }

        var activity = activityQueryService.handle(new GetActivityByIdQuery(activityId));
        return activity
                .map(ActivityResourceFromEntityAssembler::toResourceFromEntity)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete activity", description = "Marks an activity as COMPLETED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity completed"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @Parameter(name = "id", description = "Activity ID", required = true)
    public ResponseEntity<ActivityResource> completeActivity(@PathVariable Long id) {
        var command = new CompleteActivityCommand(id);
        activityCommandService.handle(command);

        var activity = activityQueryService.handle(new GetActivityByIdQuery(id));
        return activity
                .map(ActivityResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
