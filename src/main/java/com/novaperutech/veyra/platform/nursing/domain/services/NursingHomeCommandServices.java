package com.novaperutech.veyra.platform.nursing.domain.services;


import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateARoomToTheNursingHomeCommand;
import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateNursingHomeCommand;

public interface NursingHomeCommandServices {
    Long handle (CreateNursingHomeCommand command);
    void handle(CreateARoomToTheNursingHomeCommand command);

}
