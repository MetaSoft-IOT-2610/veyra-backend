package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.DeleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.UpdateActivityCommand;

public interface ActivityCommandService {
    Long handle(CreateActivityCommand command);
    void handle(UpdateActivityCommand command);
    void handle(DeleteActivityCommand command);
    void handle(CompleteActivityCommand command);
}
