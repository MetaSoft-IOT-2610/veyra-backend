package com.metasoft.veyra.platform.tracking.domain.model.commands;

import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.IotStatus;

public record ChangeIotStatusCommand(Long deviceId, IotStatus iotStatus) {
}
