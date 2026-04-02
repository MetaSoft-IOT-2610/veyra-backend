package com.novaperutech.veyra.platform.hcm.domain.services;

import com.novaperutech.veyra.platform.hcm.domain.model.aggregates.Staff;
import com.novaperutech.veyra.platform.hcm.domain.model.entities.Contract;
import com.novaperutech.veyra.platform.hcm.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface StaffQueryServices {
    Optional<Staff>handle(GetStaffByIdQuery query);
    List<Staff>handle(GetAllStaffMemberByNursingHomeIdQuery query);
    Optional<Contract>handle(GetActiveContractByStaffMemberIdQuery query);
    List<Contract>handle(GetAllContractsByStaffMemberIdQuery query);
    Optional<Contract>handle(GetContractByStaffMemberIdAndContractIdQuery query);
    Optional<Contract>handle(GetLastAddedContractByStaffMemberIdQuery query);
}
