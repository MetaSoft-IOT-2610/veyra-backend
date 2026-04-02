package com.novaperutech.veyra.platform.payments.domain.model.queries;

import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.UserId;

public record GetPaymentsByUserIdQuery(UserId userId) {
}