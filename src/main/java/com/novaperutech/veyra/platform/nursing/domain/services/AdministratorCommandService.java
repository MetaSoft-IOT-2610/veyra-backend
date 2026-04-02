package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateAdministratorCommand;

public interface AdministratorCommandService {
    Long handle(CreateAdministratorCommand command);
}
