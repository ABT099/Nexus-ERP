package com.nexus.stripe;

import jakarta.validation.constraints.NotEmpty;

public record UpdateSubscriptionRequest(
        @NotEmpty String subscriptionId,
        @NotEmpty String newPriceId
) {
}
