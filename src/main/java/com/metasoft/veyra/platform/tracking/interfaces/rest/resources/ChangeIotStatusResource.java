package com.metasoft.veyra.platform.tracking.interfaces.rest.resources;

import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.IotStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeIotStatusResource(@NotNull IotStatus iotStatus) {
}
