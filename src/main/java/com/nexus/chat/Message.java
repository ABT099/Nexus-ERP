package com.nexus.chat;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;

@Entity
public class Message extends AbstractPersistable<Long> {
    @ManyToOne(
            optional = false,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "sender_id",
            nullable = false,
            updatable = false
    )
    private User sender;

    @ManyToOne(
            optional = false,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "chat_id",
            nullable = false,
            updatable = false
    )
    private Chat chat;
    private String text;
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    public Message(User sender, Chat chat, String text) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
    }

    public Message() {}

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
