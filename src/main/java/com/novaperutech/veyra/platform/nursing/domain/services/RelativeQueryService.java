package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Relative;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetRelativeByIdQuery;

import java.util.Optional;
public interface RelativeQueryService {
    Optional<Relative>handle(GetRelativeByIdQuery query);
}
