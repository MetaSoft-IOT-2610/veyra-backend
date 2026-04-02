package com.novaperutech.veyra.platform.health.domain.model.queries;

import com.novaperutech.veyra.platform.health.domain.model.valueobjects.ResidentId;

public record GetVitalSignsByResidentIdQuery(ResidentId residentId) {
}
