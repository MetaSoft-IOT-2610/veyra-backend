package com.metasoft.veyra.platform.health.interfaces.rest.resources;

import java.util.List;

public record EdgeVitalSignThresholdsResponse(
        List<EdgeVitalSignThresholdResource> thresholds
) {
}
