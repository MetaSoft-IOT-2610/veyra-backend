package com.novaperutech.veyra.platform.activities.application.internal.commandservices;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityCommandService;
import com.novaperutech.veyra.platform.activities.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityCommandServiceImpl implements ActivityCommandService {

    private final ActivityRepository activityRepository;

    public ActivityCommandServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Long handle(CreateActivityCommand command) {
        var activity = new Activity(command);
        activityRepository.save(activity);
        return activity.getId();
    }

    @Override
    public void handle(CompleteActivityCommand command) {
        var activity = activityRepository.findById(command.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity with ID " + command.activityId() + " not found."));
        activity.complete();
        activityRepository.save(activity);
    }
}
