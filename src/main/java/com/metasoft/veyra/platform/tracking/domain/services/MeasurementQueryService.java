package com.metasoft.veyra.platform.tracking.domain.services;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetMeasurementsByDeviceIdQuery;

import java.util.List;

public interface MeasurementQueryService {
    List<Measurement> handle(GetMeasurementsByDeviceIdQuery query);
}
