package com.metasoft.veyra.platform.tracking.infrastructure.persistence.mongodb.repositories;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Measurement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends MongoRepository<Measurement,String> {

    List<Measurement> findByDeviceId_DeviceIdOrderByTimestampDesc(Long deviceId, Pageable pageable);
}
