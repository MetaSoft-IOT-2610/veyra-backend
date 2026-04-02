package com.novaperutech.veyra.platform.tracking.domain.services;

import com.novaperutech.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetAllMeasurementQuery;

import java.util.List;

public interface MeasurementQueryService {
    List<Measurement>handle(GetAllMeasurementQuery query);
}
