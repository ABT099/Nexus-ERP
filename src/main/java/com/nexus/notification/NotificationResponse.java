package com.nexus.notification;

public record NotificationResponse(
    Long id,
    String title,
    String body,
    String date,
    boolean isRead,
    NotificationType type
) { }
