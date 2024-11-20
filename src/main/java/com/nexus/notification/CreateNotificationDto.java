package com.nexus.notification;

import com.nexus.validation.EnumValue;

import java.util.Date;

public record CreateNotificationDto(
        long userId,
        String title,
        String body,
        @EnumValue(enumClass = NotificationType.class)
        NotificationType type
) { }
