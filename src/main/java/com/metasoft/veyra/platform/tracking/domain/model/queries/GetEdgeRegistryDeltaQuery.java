package com.metasoft.veyra.platform.tracking.domain.model.queries;

import java.time.LocalDateTime;

public record GetEdgeRegistryDeltaQuery(Long nursingHomeId, LocalDateTime since) {
}
