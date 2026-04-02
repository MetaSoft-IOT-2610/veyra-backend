package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateRelativeCommand;

public interface RelativeCommandService {
    Long handle(CreateRelativeCommand command);
}
