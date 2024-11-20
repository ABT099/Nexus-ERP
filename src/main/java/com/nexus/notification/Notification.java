package com.nexus.notification;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Date;

@Entity
public class Notification extends AbstractPersistable<Long> {
    @ManyToOne(cascade = CascadeType.PERSIST)
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
    private Date date;
    @Column(nullable = false)
    private boolean read = false;

    public Notification(User user, String title, String body, Date date) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
