package com.novaperutech.veyra.platform.payments.domain.services;

import com.novaperutech.veyra.platform.payments.domain.model.aggregates.Payment;
import com.novaperutech.veyra.platform.payments.domain.model.commands.ProcessPaymentCommand;

import java.util.Optional;

public interface PaymentCommandService {
    Optional<Payment> handle(ProcessPaymentCommand command);
}
