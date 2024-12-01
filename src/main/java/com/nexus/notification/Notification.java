package com.nexus.notification;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
public class Notification extends AbstractPersistable<Long> {
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;
    @Column(nullable = false, columnDefinition = "text")
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String body;
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime date = ZonedDateTime.now();
    @Column(nullable = false)
    private boolean read = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification(User user, String title, String body, NotificationType type) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public Notification() {}

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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
