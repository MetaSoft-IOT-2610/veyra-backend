package com.novaperutech.veyra.platform.payments.domain.model.valueobjects;
public enum SubscriptionStatus {
    ACTIVE,
    PAST_DUE,
    CANCELED,
    INCOMPLETE,
    INCOMPLETE_EXPIRED,
    TRIALING,
    UNPAID
}