package com.novaperutech.veyra.platform.activities.interfaces.rest;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.DeleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivityByIdQuery;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityCommandService;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityQueryService;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.ActivityResource;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.CreateActivityResource;
import com.novaperutech.veyra.platform.activities.interfaces.rest.resources.UpdateActivityResource;
import com.novaperutech.veyra.platform.activities.interfaces.rest.transform.ActivityResourceFromEntityAssembler;
import com.novaperutech.veyra.platform.activities.interfaces.rest.transform.CreateActivityCommandFromResourceAssembler;
import com.novaperutech.veyra.platform.activities.interfaces.rest.transform.UpdateActivityCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * ActivitiesController
 * <p>All activity-related endpoints, scoped to a specific nursing home.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/nursing-homes/{nursingHomeId}/activities", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Activities", description = "Activities Management Endpoints")
public class ActivitiesController {

    private final ActivityCommandService activityCommandService;
    private final ActivityQueryService activityQueryService;

    /**
     * Constructor.
     * @param activityCommandService the {@link ActivityCommandService} instance
     * @param activityQueryService the {@link ActivityQueryService} instance
     */
    public ActivitiesController(ActivityCommandService activityCommandService, ActivityQueryService activityQueryService) {
        this.activityCommandService = activityCommandService;
        this.activityQueryService = activityQueryService;
    }

    @GetMapping
    @Operation(summary = "Get activities by nursing home", description = "Returns all activities for a given nursing home.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Activities retrieved successfully"))
    /**
     * Returns all activities for a given nursing home.
     * @param nursingHomeId the nursing home ID from the request path
     * @return a list of {@link ActivityResource} for the nursing home
     */
    public ResponseEntity<List<ActivityResource>> getActivitiesByNursingHome(@PathVariable Long nursingHomeId) {
        var activities = activityQueryService.handle(new GetActivitiesByNursingHomeIdQuery(nursingHomeId));
        var resources = activities.stream()
                .map(ActivityResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    @Operation(summary = "Create activity", description = "Registers a new activity with status PENDING.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Activity created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    /**
     * Creates a new activity within the given nursing home.
     * @param nursingHomeId the nursing home ID from the request path
     * @param resource the {@link CreateActivityResource} with the activity data
     * @return the created {@link ActivityResource} with HTTP 201
     */
    public ResponseEntity<ActivityResource> createActivity(@PathVariable Long nursingHomeId, @RequestBody CreateActivityResource resource) {
        var command = CreateActivityCommandFromResourceAssembler.toCommandFromResource(nursingHomeId, resource);
        var activityId = activityCommandService.handle(command);
        if (activityId == null || activityId == 0L) return ResponseEntity.badRequest().build();
        var activity = activityQueryService.handle(new GetActivityByIdQuery(activityId));
        if (activity.isEmpty()) return ResponseEntity.badRequest().build();
        var activityResource = ActivityResourceFromEntityAssembler.toResourceFromEntity(activity.get());
        return new ResponseEntity<>(activityResource, HttpStatus.CREATED);
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "Update activity", description = "Updates type, title, isRecurring and recurringDays of an existing activity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity updated"),
            @ApiResponse(responseCode = "400", description = "Activity not found or invalid input")
    })
    /**
     * Updates an existing activity.
     * @param nursingHomeId the nursing home ID from the request path
     * @param activityId the activity ID from the request path
     * @param resource the {@link UpdateActivityResource} with the new values
     * @return the updated {@link ActivityResource}
     */
    public ResponseEntity<ActivityResource> updateActivity(@PathVariable Long nursingHomeId, @PathVariable Long activityId, @RequestBody UpdateActivityResource resource) {
        var command = UpdateActivityCommandFromResourceAssembler.toCommandFromResource(activityId, resource);
        var updated = activityCommandService.handle(command);
        if (updated.isEmpty()) return ResponseEntity.badRequest().build();
        var activityResource = ActivityResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(activityResource);
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "Delete activity", description = "Deletes an activity by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity deleted"),
            @ApiResponse(responseCode = "400", description = "Activity not found")
    })
    /**
     * Deletes an activity by its ID.
     * @param nursingHomeId the nursing home ID from the request path
     * @param activityId the activity ID to delete
     * @return a success message with HTTP 200
     */
    public ResponseEntity<?> deleteActivity(@PathVariable Long nursingHomeId, @PathVariable Long activityId) {
        activityCommandService.handle(new DeleteActivityCommand(activityId));
        return ResponseEntity.ok("Activity with given id successfully deleted");
    }

    @PatchMapping("/{activityId}/complete")
    @Operation(summary = "Advance activity status", description = "PENDING → IN_PROGRESS → COMPLETED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status advanced"),
            @ApiResponse(responseCode = "400", description = "Activity not found"),
            @ApiResponse(responseCode = "409", description = "Activity already completed")
    })
    /**
     * Advances the status of an activity through its lifecycle (PENDING → IN_PROGRESS → COMPLETED).
     * @param nursingHomeId the nursing home ID from the request path
     * @param activityId the activity ID to advance
     * @return the updated {@link ActivityResource} after the status transition
     */
    public ResponseEntity<ActivityResource> advanceActivityStatus(@PathVariable Long nursingHomeId, @PathVariable Long activityId) {
        activityCommandService.handle(new CompleteActivityCommand(activityId));
        var activity = activityQueryService.handle(new GetActivityByIdQuery(activityId));
        if (activity.isEmpty()) return ResponseEntity.badRequest().build();
        var activityResource = ActivityResourceFromEntityAssembler.toResourceFromEntity(activity.get());
        return ResponseEntity.ok(activityResource);
    }
}
