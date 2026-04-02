package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByDateAndNursingHomeQuery;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityView;

import java.util.List;

public interface ActivityQueryService {
    List<ActivityView> handle(GetActivitiesByDateAndNursingHomeQuery query);
}