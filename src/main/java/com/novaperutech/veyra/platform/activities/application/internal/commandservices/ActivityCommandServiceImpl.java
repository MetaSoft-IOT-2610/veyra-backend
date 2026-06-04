package com.novaperutech.veyra.platform.activities.application.internal.commandservices;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.DeleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.UpdateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityCommandService;
import com.novaperutech.veyra.platform.activities.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityCommandServiceImpl implements ActivityCommandService {

    private final ActivityRepository activityRepository;

    public ActivityCommandServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Long handle(CreateActivityCommand command) {
        var activity = new Activity(command);
        try {
            activityRepository.save(activity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving activity: %s".formatted(e.getMessage()));
        }
        return activity.getId();
    }

    @Override
    public Optional<Activity> handle(UpdateActivityCommand command) {
        var result = activityRepository.findById(command.activityId());
        if (result.isEmpty())
            throw new IllegalArgumentException("Activity with id %s not found".formatted(command.activityId()));
        var activityToUpdate = result.get();
        try {
            var updated = activityRepository.save(activityToUpdate.update(command));
            return Optional.of(updated);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while updating activity: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void handle(DeleteActivityCommand command) {
        if (!activityRepository.existsById(command.activityId()))
            throw new IllegalArgumentException("Activity with id %s not found".formatted(command.activityId()));
        try {
            activityRepository.deleteById(command.activityId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting activity: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void handle(CompleteActivityCommand command) {
        var activity = activityRepository.findById(command.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity with id %s not found".formatted(command.activityId())));
        activity.advance();
        try {
            activityRepository.save(activity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while advancing activity: %s".formatted(e.getMessage()));
        }
    }
}
