package com.nexus.notification;

public record CreateNotificationDto(
        long userId,
        String title,
        String body,
        NotificationType type
) { }
