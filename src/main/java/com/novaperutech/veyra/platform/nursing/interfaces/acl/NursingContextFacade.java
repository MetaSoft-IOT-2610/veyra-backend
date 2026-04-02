package com.novaperutech.veyra.platform.nursing.interfaces.acl;

public interface NursingContextFacade {
    Long fetchNursingHomeById(Long id);
    boolean existsResidentByPersonProfile(Long personProfile);
    Long fetchResidentById(Long residentId);
}