package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateMedicationCommand;

public interface MedicationCommandServices {
    Long handle (CreateMedicationCommand command);
}
