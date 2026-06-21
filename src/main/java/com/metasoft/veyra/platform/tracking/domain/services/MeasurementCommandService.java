package com.metasoft.veyra.platform.tracking.domain.services;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordMeasurementCommand;

import java.util.Optional;

public interface MeasurementCommandService {
Optional<Measurement>handle(RecordMeasurementCommand command);
}
