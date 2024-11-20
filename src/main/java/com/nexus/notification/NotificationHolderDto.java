package com.nexus.notification;

public class NotificationHolderDto {
    private Long userId;
    private String title;
    private String body;
    private NotificationType type;

    public NotificationHolderDto(Long userId, String title, String body, NotificationType type) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
