package com.metasoft.veyra.platform.tracking.domain.services;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Device;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetDeviceByIdQuery;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetDevicesByNursingHomeIdQuery;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetDevicesByResidentIdQuery;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetEdgeRegistryDeltaQuery;
import com.metasoft.veyra.platform.tracking.domain.model.queries.GetUnassignedDevicesQuery;

import java.util.List;
import java.util.Optional;

public interface DeviceQueryService {
    Optional<Device> handle(GetDeviceByIdQuery query);

    List<Device> handle(GetDevicesByResidentIdQuery query);

    List<Device> handle(GetUnassignedDevicesQuery query);

    List<Device> handle(GetDevicesByNursingHomeIdQuery query);

    List<Device> handle(GetEdgeRegistryDeltaQuery query);
}
