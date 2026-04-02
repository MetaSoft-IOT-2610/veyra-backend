package com.novaperutech.veyra.platform.health.application.internal.queryservices;

import com.novaperutech.veyra.platform.health.domain.model.aggregates.VitalSign;
import com.novaperutech.veyra.platform.health.domain.model.queries.GetVitalSignByIdQuery;
import com.novaperutech.veyra.platform.health.domain.model.queries.GetVitalSignsByResidentIdQuery;
import com.novaperutech.veyra.platform.health.domain.services.VitalSignQueryService;
import com.novaperutech.veyra.platform.health.infrastructure.persistence.jpa.repositories.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VitalSignQueryServiceImpl implements VitalSignQueryService {
    private final VitalSignRepository vitalSignRepository;

    public VitalSignQueryServiceImpl(VitalSignRepository vitalSignRepository) {
        this.vitalSignRepository = vitalSignRepository;
    }

    @Override
    public List<VitalSign> handle(GetVitalSignsByResidentIdQuery query) {
        return vitalSignRepository.findAllByResidentId(query.residentId());
    }

    @Override
    public Optional<VitalSign> handle(GetVitalSignByIdQuery query) {
        return vitalSignRepository.findById(query.id());
    }
}
