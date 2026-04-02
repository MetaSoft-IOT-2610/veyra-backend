package com.novaperutech.veyra.platform.tracking.domain.services;

import com.novaperutech.veyra.platform.tracking.domain.model.aggregates.Device;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetAllDevicesQuery;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetDeviceByIdQuery;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetDevicesByResidentQuery;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetUnassignedDevicesQuery;

import java.util.List;
import java.util.Optional;

public interface DeviceQueryService {
    List<Device> handle(GetAllDevicesQuery query);
    Optional<Device> handle(GetDeviceByIdQuery query);
    List<Device> handle(GetDevicesByResidentQuery query);
    List<Device> handle(GetUnassignedDevicesQuery query);
}
