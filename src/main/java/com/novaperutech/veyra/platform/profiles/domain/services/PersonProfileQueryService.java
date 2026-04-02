package com.novaperutech.veyra.platform.profiles.domain.services;

import com.novaperutech.veyra.platform.profiles.domain.model.aggregates.PersonProfile;
import com.novaperutech.veyra.platform.profiles.domain.model.queries.GetAllPersonProfileQuery;
import com.novaperutech.veyra.platform.profiles.domain.model.queries.GetPersonProfileByDniQuery;
import com.novaperutech.veyra.platform.profiles.domain.model.queries.GetPersonProfileByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PersonProfileQueryService {
    List<PersonProfile> handle(GetAllPersonProfileQuery query);
    Optional<PersonProfile>handle(GetPersonProfileByIdQuery query);
    Optional<PersonProfile>handle(GetPersonProfileByDniQuery query);
}
