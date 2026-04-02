package com.novaperutech.veyra.platform.nursing.domain.model.commands;

public record AssignedStaffMemberToResidentCommand(Long nursingHomeId,Long residentId,Long staffMemberId) {
}
