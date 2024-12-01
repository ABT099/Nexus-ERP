package com.nexus.chat;

import java.time.ZonedDateTime;
import java.util.Date;

public record MessageResponse(
    Long id,
    Long senderId,
    Long chatId,
    String text,
    Date createdAt
) { }
