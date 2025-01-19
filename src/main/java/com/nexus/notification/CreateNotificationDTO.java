package com.nexus.notification;

public record CreateNotificationDTO(
        long userId,
        String title,
        String body,
        NotificationType type
) { }
