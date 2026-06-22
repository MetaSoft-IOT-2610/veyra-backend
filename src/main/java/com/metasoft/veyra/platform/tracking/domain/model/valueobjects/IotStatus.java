package com.metasoft.veyra.platform.tracking.domain.model.valueobjects;

/**
 * IoT authorization lifecycle for edge registry sync and telemetry ingestion.
 * Separate from {@link AssignmentStatus} (resident assignment workflow).
 */
public enum IotStatus {
    ACTIVE,
    REVOKED
}
