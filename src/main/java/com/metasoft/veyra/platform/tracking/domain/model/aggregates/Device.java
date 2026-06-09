package com.metasoft.veyra.platform.tracking.domain.model.aggregates;

import com.metasoft.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.AssignmentStatus;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Device extends AuditableAbstractAggregateRoot<Device> {

    @Column(nullable = false, unique = true, length = 50)
    private String deviceId;

    private Long residentId;

    private Long nursingHomeId;

    @Column(length = 100)
    private String assignedBy;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeviceType deviceType;

    protected Device() {}

    public Device(String deviceId) {
        this.deviceId = deviceId;
        this.residentId = null;
        this.assignedBy = "";
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.UNASSIGNED;
    }

    public Device(String deviceId, Long residentId, String assignedBy) {
        this.deviceId = deviceId;
        this.residentId = residentId;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.ACTIVE;
    }

    public Device(Long nursingHomeId, DeviceType deviceType, String macAddress) {
        this.deviceId = macAddress;
        this.nursingHomeId = nursingHomeId;
        this.deviceType = deviceType;
        this.assignedBy = "";
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.UNASSIGNED;
    }

    public void assignToResident(Long residentId, String assignedBy) {
        this.residentId = residentId;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.ACTIVE;
    }

    public void unassign() {
        this.residentId = null;
        this.assignedBy = null;
        this.status = AssignmentStatus.UNASSIGNED;
    }

    public void updateType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public void deactivate() {
        this.status = AssignmentStatus.INACTIVE;
    }

    public boolean isAssigned() {
        return this.residentId != null && this.status == AssignmentStatus.ACTIVE;
    }
}