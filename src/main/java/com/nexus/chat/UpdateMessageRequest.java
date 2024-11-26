package com.nexus.chat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record UpdateMessageRequest(
        @Positive long id,
        @NotEmpty String text) {
}
