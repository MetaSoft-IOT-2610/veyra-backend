package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByResidentIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivityByIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetAllActivitiesQuery;

import java.util.List;
import java.util.Optional;

public interface ActivityQueryService {
    List<Activity> handle(GetAllActivitiesQuery query);
    List<Activity> handle(GetActivitiesByNursingHomeIdQuery query);
    List<Activity> handle(GetActivitiesByResidentIdQuery query);
    Optional<Activity> handle(GetActivityByIdQuery query);
}
