package com.nexus.chat;

import java.time.ZonedDateTime;

public record MessageResponse(
    Long id,
    Long senderId,
    Long chatId,
    String text,
    ZonedDateTime createdAt
) { }
