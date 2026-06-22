package com.metasoft.veyra.platform.tracking.application.internal.queryservices;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetMeasurementsByDeviceIdQuery;
import com.metasoft.veyra.platform.tracking.domain.services.MeasurementQueryService;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.mongodb.repositories.MeasurementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementQueryServiceImpl implements MeasurementQueryService {
private final MeasurementRepository measurementRepository;

    public MeasurementQueryServiceImpl(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public List<Measurement> handle(GetMeasurementsByDeviceIdQuery query) {
        var limit = Math.max(1, Math.min(query.limit(), 200));
        return measurementRepository.findByDeviceId_DeviceIdOrderByTimestampDesc(
                query.deviceId(),
                PageRequest.of(0, limit)
        );
    }
}
