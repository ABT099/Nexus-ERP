package com.nexus.notification;

public record NotificationDTO (
        Long userId,
        String title,
        String body,
        NotificationType type
) { }
