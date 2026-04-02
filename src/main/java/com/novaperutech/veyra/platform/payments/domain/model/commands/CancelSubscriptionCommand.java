package com.novaperutech.veyra.platform.payments.domain.model.commands;

public record CancelSubscriptionCommand(Long subscriptionId) {
    public CancelSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}