package com.nexus.chat;

public record MessageResponse(
    Long id,
    Long senderId,
    Long chatId,
    String text,
    String createdAt
) { }
