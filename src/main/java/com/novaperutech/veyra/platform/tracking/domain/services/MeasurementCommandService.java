package com.novaperutech.veyra.platform.tracking.domain.services;

import com.novaperutech.veyra.platform.tracking.domain.model.commands.SeedMeasurementCommand;

public interface MeasurementCommandService {
    void handle(SeedMeasurementCommand command);

}
