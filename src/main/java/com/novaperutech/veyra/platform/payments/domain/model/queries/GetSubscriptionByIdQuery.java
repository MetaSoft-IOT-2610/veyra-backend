package com.novaperutech.veyra.platform.payments.domain.model.queries;

public record GetSubscriptionByIdQuery(Long subscriptionId) {
    public GetSubscriptionByIdQuery {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}