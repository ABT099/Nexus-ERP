package com.nexus.chat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record MessageRequest(
        @Positive Long senderId,
        @Positive Long chatId,
        @NotEmpty String receiverUsername,
        @NotEmpty String text
) {
}
