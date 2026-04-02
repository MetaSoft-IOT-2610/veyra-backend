package com.novaperutech.veyra.platform.health.domain.services;

import com.novaperutech.veyra.platform.health.domain.model.commands.ValidateVitalSignCommand;

public interface VitalSignCommandService {
    void handle(ValidateVitalSignCommand command);
}
