package com.novaperutech.veyra.platform.activities.domain.model.aggregates;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityStatus;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "activities")
@Getter
public class Activity extends AuditableAbstractAggregateRoot<Activity> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(nullable = false)
    private Long residentId;

    @Column(nullable = false)
    private Long healthcareStaffId;

    protected Activity() {}

    public Activity(CreateActivityCommand command) {
        this.type = command.type();
        this.status = ActivityStatus.IN_PROGRESS;
        this.residentId = command.residentId();
        this.healthcareStaffId = command.healthcareStaffId();
    }

    public void complete() {
        if (this.status == ActivityStatus.COMPLETED) {
            throw new IllegalStateException("Activity is already completed.");
        }
        this.status = ActivityStatus.COMPLETED;
    }
}
