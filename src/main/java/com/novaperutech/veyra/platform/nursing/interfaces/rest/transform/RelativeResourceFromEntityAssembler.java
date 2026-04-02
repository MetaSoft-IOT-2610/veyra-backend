package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Relative;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.RelativeResource;

public class RelativeResourceFromEntityAssembler {
    public static RelativeResource toResourceFromEntity(Relative entity) {
        return new RelativeResource(entity.getId(),entity.getUserId().userId());
    }
}
