package com.metasoft.veyra.platform.tracking.application.internal.commandservices;

import com.metasoft.veyra.platform.tracking.domain.exceptions.DeviceAlreadyExistsException;
import com.metasoft.veyra.platform.tracking.domain.model.aggregates.Device;
import com.metasoft.veyra.platform.tracking.domain.model.commands.AssignDeviceCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.ChangeDeviceStatusCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.ChangeIotStatusCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.RegisterDeviceCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.UnassignDeviceCommand;
import com.metasoft.veyra.platform.tracking.domain.model.commands.UpdateDeviceCommand;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceType;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.MacAddress;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.NursingHomeId;
import com.metasoft.veyra.platform.tracking.domain.services.DeviceCommandService;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);

    private final DeviceRepository deviceRepository;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Long handle(AssignDeviceCommand command) {
        var device = deviceRepository.findById(command.deviceId())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + command.deviceId()));

        device.assignToResident(command.residentId());
        deviceRepository.save(device);

        LOGGER.info("Device {} assigned to resident {}", command.deviceId(), command.residentId());
        return device.getId();
    }

    @Override
    public void handle(UnassignDeviceCommand command) {
        var device = deviceRepository.findById(command.deviceId())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + command.deviceId()));

        device.unassign();
        deviceRepository.save(device);

        LOGGER.info("Device {} unassigned", command.deviceId());
    }

    @Override
    public Long handle(RegisterDeviceCommand command) {
        var nursingHomeId = new NursingHomeId(command.nursingHomeId());
        var deviceType = DeviceType.valueOf(command.deviceType().toUpperCase());
        var macAddress = new MacAddress(command.macAddress());

        if (deviceRepository.existsByExternalDeviceId(command.externalDeviceId().trim())) {
            throw new DeviceAlreadyExistsException(
                    "Device already exists for external id '" + command.externalDeviceId() + "'");
        }
        if (deviceRepository.existsByMacAddress(macAddress)) {
            throw new DeviceAlreadyExistsException("Device already exists for mac address");
        }
        if (deviceType == DeviceType.EDGE_GATEWAY
                && deviceRepository.existsByNursingHomeIdAndDeviceType(nursingHomeId, DeviceType.EDGE_GATEWAY)) {
            throw new DeviceAlreadyExistsException("This nursing home already has an edge gateway registered");
        }

        var device = new Device(
                command.nursingHomeId(),
                command.externalDeviceId(),
                command.deviceType(),
                command.macAddress()
        );
        deviceRepository.save(device);
        LOGGER.info("Device {} registered for nursing home {}", command.externalDeviceId(), command.nursingHomeId());
        return device.getId();
    }

    @Override
    public Long handle(UpdateDeviceCommand command) {
        var device = deviceRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + command.id()));

        var macAddress = new MacAddress(command.macAddress());
        deviceRepository.findByMacAddress(macAddress).ifPresent(existing -> {
            if (!existing.getId().equals(command.id())) {
                throw new DeviceAlreadyExistsException("Device already exists for mac address");
            }
        });
        deviceRepository.findByExternalDeviceId(command.externalDeviceId().trim()).ifPresent(existing -> {
            if (!existing.getId().equals(command.id())) {
                throw new DeviceAlreadyExistsException("Device already exists for external id");
            }
        });

        device.updateDevice(command.externalDeviceId(), command.deviceType(), command.macAddress());
        deviceRepository.save(device);
        LOGGER.info("Device {} updated", command.id());
        return device.getId();
    }

    @Override
    public Long handle(ChangeDeviceStatusCommand command) {
        var device = deviceRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + command.id()));
        device.changeStatus(command.status());
        deviceRepository.save(device);
        LOGGER.info("Device {} status changed to {}", command.id(), command.status());
        return device.getId();
    }

    @Override
    public Long handle(ChangeIotStatusCommand command) {
        var device = deviceRepository.findById(command.deviceId())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + command.deviceId()));
        device.changeIotStatus(command.iotStatus());
        deviceRepository.save(device);
        LOGGER.info("Device {} IoT status changed to {}", command.deviceId(), command.iotStatus());
        return device.getId();
    }
}
