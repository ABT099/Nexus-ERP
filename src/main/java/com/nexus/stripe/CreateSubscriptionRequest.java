package com.nexus.stripe;

import jakarta.validation.constraints.NotEmpty;

public record CreateSubscriptionRequest(
        @NotEmpty String customerId,
        @NotEmpty String priceId
) { }
