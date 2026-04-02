package com.novaperutech.veyra.platform.payments.interfaces.rest.resources;

public record ProcessPaymentResource(
        Long subscriptionId,
        String paymentMethodId
) {}
