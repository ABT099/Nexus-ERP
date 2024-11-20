package com.nexus.notification;

import java.util.Date;

public record CreateNotificationDto(
        long userId,
        String title,
        String body,
        Date date
) { }
