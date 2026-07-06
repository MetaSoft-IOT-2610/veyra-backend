package com.metasoft.veyra.platform.activities.entities;

import com.metasoft.veyra.platform.activities.domain.model.aggregates.Activity;
import com.metasoft.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.metasoft.veyra.platform.activities.domain.model.valueobjects.ActivityStatus;
import com.metasoft.veyra.platform.activities.domain.model.valueobjects.ActivityType;
import com.metasoft.veyra.platform.activities.domain.model.valueobjects.RecurringDay;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ActivityAggregateTest {

    private CreateActivityCommand buildCommand() {
        return new CreateActivityCommand(
                1L,
                2L,
                3L,
                ActivityType.valueOf("OCCUPATIONAL_THERAPY"),
                "Terapia ocupacional",
                true,
                List.of(RecurringDay.valueOf("MONDAY"), RecurringDay.valueOf("WEDNESDAY"))
        );
    }

    @Test
    void shouldInitializeWithPendingStatus() {
        Activity activity = new Activity(buildCommand());

        assertEquals(ActivityStatus.PENDING, activity.getStatus(),
                "Una actividad nueva debe iniciar con estado PENDING");
    }

}
