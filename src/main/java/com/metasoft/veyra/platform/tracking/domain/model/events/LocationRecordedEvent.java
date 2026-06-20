package com.metasoft.veyra.platform.tracking.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;
@Getter
public class LocationRecordedEvent extends ApplicationEvent {
    private final Long deviceId;
    private final double latitude;
    private final double longitude;
    private final LocalDateTime recordedAt;

    public LocationRecordedEvent(Object source, Long deviceId, double latitude, double longitude, LocalDateTime recordedAt) {
        super(source);
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }
}