package com.metasoft.veyra.platform.tracking.domain.services;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Location;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordLocationCommand;

import java.util.Optional;

public interface LocationCommandService {
    Optional<Location> handle(RecordLocationCommand command);
}
