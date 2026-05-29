package com.metasoft.veyra.platform.nursing.domain.model.commands;

public record AssignResidentForRelativeCommand(Long residentId,Long relativeId) {
    public AssignResidentForRelativeCommand{
        if (relativeId==null||residentId==null){
            throw new IllegalArgumentException("ResidentId and RelativeId cannot be null");
        }
    }
}
