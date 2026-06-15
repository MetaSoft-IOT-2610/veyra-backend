package com.metasoft.veyra.platform.nursing.interfaces.acl;

public interface NursingContextFacade {
  Long fetchNursingHomeById(Long id);
  boolean existsResidentByPersonProfile(Long personProfile);
  Long fetchResidentById(Long residentId);
  Long fetchAdministratorByUserId(Long userId);
  Long fetchStaffByUserId(Long userId);
}