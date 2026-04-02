package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;

public interface ActivityCommandService {
    Long handle(CreateActivityCommand command);
    void handle(CompleteActivityCommand command);
}