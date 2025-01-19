package com.nexus.notification;

import com.nexus.utils.Mapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements Mapper<Notification, NotificationResponse> {

    @Override
    public NotificationResponse map(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getBody(),
            notification.getDate(),
            notification.isRead(),
            notification.getType()
        );
    }
}
