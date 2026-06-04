package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CompleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.DeleteActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.UpdateActivityCommand;

import java.util.Optional;

public interface ActivityCommandService {
    Long handle(CreateActivityCommand command);
    Optional<Activity> handle(UpdateActivityCommand command);
    void handle(DeleteActivityCommand command);
    void handle(CompleteActivityCommand command);
}
