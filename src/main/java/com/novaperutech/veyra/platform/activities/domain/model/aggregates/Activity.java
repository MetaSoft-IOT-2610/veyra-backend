package com.novaperutech.veyra.platform.activities.domain.model.aggregates;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.commands.UpdateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityStatus;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.RecurringDay;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activities")
@Getter
public class Activity extends AuditableAbstractAggregateRoot<Activity> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(nullable = false)
    private Long residentId;

    @Column(nullable = false)
    private Long healthcareStaffId;

    @Column(nullable = false)
    private Boolean isRecurring;

    @ElementCollection(targetClass = RecurringDay.class)
    @CollectionTable(name = "activity_recurring_days", joinColumns = @JoinColumn(name = "activity_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private List<RecurringDay> recurringDays = new ArrayList<>();

    protected Activity() {}

    public Activity(CreateActivityCommand command) {
        this.type = command.type();
        this.title = command.title();
        this.status = ActivityStatus.PENDING;
        this.residentId = command.residentId();
        this.healthcareStaffId = command.healthcareStaffId();
        this.isRecurring = command.isRecurring();
        this.recurringDays = command.recurringDays() != null ? command.recurringDays() : new ArrayList<>();
    }

    public void update(UpdateActivityCommand command) {
        this.type = command.type();
        this.title = command.title();
        this.isRecurring = command.isRecurring();
        this.recurringDays = command.recurringDays() != null ? command.recurringDays() : new ArrayList<>();
    }

    public void advance() {
        if (this.status == ActivityStatus.COMPLETED) {
            throw new IllegalStateException("Activity is already completed.");
        }
        this.status = this.status == ActivityStatus.PENDING
                ? ActivityStatus.IN_PROGRESS
                : ActivityStatus.COMPLETED;
    }
}
