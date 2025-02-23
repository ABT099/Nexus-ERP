package com.nexus.stripe;

public record SubscriptionResponse(
        String subscriptionId,
        String clientSecret
) { }
