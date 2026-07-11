package com.metasoft.veyra.platform.tracking.application.internal.commandservices;

import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Location;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RecordLocationCommand;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.MacAddress;
import com.metasoft.veyra.platform.tracking.domain.services.LocationCommandService;
import com.metasoft.veyra.platform.tracking.infrastructure.authorization.MacAddressNormalizer;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.mongodb.repositories.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationCommandServiceImpl implements LocationCommandService {

    private final LocationRepository locationRepository;
    private final DeviceRepository deviceRepository;

    public LocationCommandServiceImpl(LocationRepository gpsLocationRepository, DeviceRepository deviceRepository) {
        this.locationRepository = gpsLocationRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Optional<Location> handle(RecordLocationCommand command) {
        var macAddress = new MacAddress(MacAddressNormalizer.normalize(command.deviceMacAddress()));
        var device = deviceRepository.findByExternalDeviceIdAndMacAddress(
                        command.deviceExternalId(),
                        macAddress)
                .orElseThrow(() -> new IllegalArgumentException("device dont exists"));

        if (!device.getNursingHomeId().nursingHomeId().equals(command.gatewayNursingHomeId())) {
            throw new IllegalStateException("Device does not belong to the authenticated gateway nursing home");
        }

        var location = new Location(device.getId(), command.latitude(), command.longitude());
        var saveLocation = locationRepository.save(location);
        return Optional.of(saveLocation);
    }
}