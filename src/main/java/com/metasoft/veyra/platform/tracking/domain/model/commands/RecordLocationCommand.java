package com.metasoft.veyra.platform.tracking.domain.model.commands;

public record RecordLocationCommand(Long  deviceId, double latitude, double longitude) {
}
