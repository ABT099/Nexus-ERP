package com.nexus.stripe;

public enum SubscriptionStatus {
    ACTIVE,
    PAST_DUE,
    CANCELED,
    UNPAID,
    INCOMPLETE,
    INCOMPLETE_EXPIRED,
    PAUSED,
    PENDING
}
