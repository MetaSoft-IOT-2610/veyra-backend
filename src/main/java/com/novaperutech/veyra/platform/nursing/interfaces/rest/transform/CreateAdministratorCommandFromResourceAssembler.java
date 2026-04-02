package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateAdministratorCommand;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.CreateAdministratorResource;

public class CreateAdministratorCommandFromResourceAssembler {
    public static CreateAdministratorCommand toCommandFromResource(CreateAdministratorResource resource) {
        return new CreateAdministratorCommand(resource.username(),resource.password());
    }
}
