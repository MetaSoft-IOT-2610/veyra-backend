package com.novaperutech.veyra.platform.nursing.domain.model.commands;

public record ChangeOfRoomForTheResidentCommand(Long nursingHomeId,String newRoomNumber,Long residentId) {
}
