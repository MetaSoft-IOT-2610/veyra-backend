package com.novaperutech.veyra.platform.activities.application.internal.queryservices;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByResidentIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivityByIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetAllActivitiesQuery;
import com.novaperutech.veyra.platform.activities.domain.services.ActivityQueryService;
import com.novaperutech.veyra.platform.activities.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityQueryServiceImpl implements ActivityQueryService {

    private final ActivityRepository activityRepository;

    public ActivityQueryServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> handle(GetAllActivitiesQuery query) {
        return activityRepository.findAll();
    }

    @Override
    public List<Activity> handle(GetActivitiesByResidentIdQuery query) {
        return activityRepository.findByResidentId(query.residentId());
    }

    @Override
    public Optional<Activity> handle(GetActivityByIdQuery query) {
        return activityRepository.findById(query.activityId());
    }
}
