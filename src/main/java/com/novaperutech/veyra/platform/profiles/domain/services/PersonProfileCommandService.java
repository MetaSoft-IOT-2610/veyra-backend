package com.novaperutech.veyra.platform.profiles.domain.services;

import com.novaperutech.veyra.platform.profiles.domain.model.aggregates.PersonProfile;
import com.novaperutech.veyra.platform.profiles.domain.model.commands.CreatePersonProfileCommand;
import com.novaperutech.veyra.platform.profiles.domain.model.commands.DeletePersonProfileCommand;
import com.novaperutech.veyra.platform.profiles.domain.model.commands.UpdatePersonProfileCommand;

import java.util.Optional;

public interface PersonProfileCommandService {
    Optional<PersonProfile> handle(CreatePersonProfileCommand command);
    Optional<PersonProfile> handle(UpdatePersonProfileCommand command);
    void handle (DeletePersonProfileCommand command);
}
