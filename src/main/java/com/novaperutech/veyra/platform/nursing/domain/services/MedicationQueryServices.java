package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllMedicationsByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MedicationQueryServices {
    Optional<Medication>handle(GetMedicationByIdQuery query);
    List<Medication>handle(GetAllMedicationsByResidentIdQuery query);
}
