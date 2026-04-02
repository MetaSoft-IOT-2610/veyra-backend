package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Administrator;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AdministratorResource;

public class AdministratorResourceFromEntityAssembler {
    public static AdministratorResource toResourceFromEntity(Administrator entity)
    {
       return new AdministratorResource(entity.getId(),entity.getUserId().userId());
    }
}
