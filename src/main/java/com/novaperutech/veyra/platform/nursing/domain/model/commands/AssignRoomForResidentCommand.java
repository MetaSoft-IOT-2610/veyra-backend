package com.novaperutech.veyra.platform.nursing.domain.model.commands;

public record AssignRoomForResidentCommand(Long nursingHomeId, String roomNumber,Long residentId) {

}
