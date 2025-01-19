package com.nexus.chat;

import java.util.Date;

public record MessageResponse(
    Long id,
    Long senderId,
    Long chatId,
    String text,
    Date createdAt
) { }
