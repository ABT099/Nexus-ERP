package com.nexus.notification;

import java.time.ZonedDateTime;

public record NotificationResponse(
    Long id,
    String title,
    String body,
    ZonedDateTime date,
    boolean isRead,
    NotificationType type
) { }
