package com.metasoft.veyra.platform.tracking.domain.model.aggregates;

import com.metasoft.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Device extends AuditableAbstractAggregateRoot<Device> {
    @Embedded
    private MacAddress macAddress;
@Embedded
    private ResidentId residentId;
@Embedded
    private NursingHomeId nursingHomeId;


    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeviceType deviceType;

    public Device() {}

    public Device(String macAddress) {
        this.macAddress = new MacAddress(macAddress);
        this.residentId = null;
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.AVAILABLE;
    }

    public Device(String macAddress, Long residentId) {
        this.macAddress = new MacAddress(macAddress);
        this.residentId = new ResidentId(residentId);
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.AVAILABLE;
    }

    public Device(Long nursingHomeId, String deviceType, String macAddress) {
        this.macAddress = new MacAddress(macAddress);
        this.nursingHomeId =new NursingHomeId( nursingHomeId);
        this.deviceType = DeviceType.valueOf(deviceType.toUpperCase());
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.AVAILABLE;
    }

    public void assignToResident(Long residentId) {
        this.residentId = new ResidentId(residentId);
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.ASSIGNED;
    }

    public void unassign() {
        this.residentId = null;
        this.status = AssignmentStatus.AVAILABLE;
    }

    public void updateDevice(String deviceType,String macAddress) {
        this.deviceType = DeviceType.valueOf(deviceType.toUpperCase());
        this.macAddress= new MacAddress(macAddress);
    }

    public void deactivate() {
        this.status = AssignmentStatus.UNAVAILABLE;
    }

    public boolean isAssigned() {
        return this.residentId != null && this.status == AssignmentStatus.AVAILABLE;
    }
}