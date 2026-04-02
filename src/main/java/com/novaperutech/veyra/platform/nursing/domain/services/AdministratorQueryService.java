package com.novaperutech.veyra.platform.nursing.domain.services;


import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Administrator;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAdministratorByIdQuery;

import java.util.Optional;

public interface AdministratorQueryService {
    Optional<Administrator> handle(GetAdministratorByIdQuery query);
}
