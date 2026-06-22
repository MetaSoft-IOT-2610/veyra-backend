package com.metasoft.veyra.platform.tracking.application.internal.eventhandlers;

import com.metasoft.veyra.platform.tracking.domain.model.events.MeasurementRecordedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class MeasurementRecordedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementRecordedEventHandler.class);
    private final SimpMessagingTemplate messagingTemplate;

    public MeasurementRecordedEventHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void on(MeasurementRecordedEvent event) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("deviceId", event.getDeviceId());
        payload.put("measurementTimestamp", event.getMeasurementTimestamp().toString());
        if (event.getHeartRate() != null) {
            payload.put("heartRate", event.getHeartRate());
        }
        if (event.getTemperature() != null) {
            payload.put("temperature", event.getTemperature());
        }
        if (event.getOxygenSaturation() != null) {
            payload.put("oxygenSaturation", event.getOxygenSaturation());
        }

        var topic = "/topic/tracking/measurements/" + event.getDeviceId();
        try {
            messagingTemplate.convertAndSend(topic, payload);
            LOGGER.info("Broadcasted measurement to topic: {} | payload: {}", topic, payload);
        } catch (Exception ex) {
            LOGGER.warn("WebSocket broadcast failed for topic {}: {}", topic, ex.getMessage());
        }
    }
}