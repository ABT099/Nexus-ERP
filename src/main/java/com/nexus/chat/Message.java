package com.nexus.chat;

import com.nexus.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
public class Message  {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(
            optional = false,
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
    private Instant createdAt = Instant.now();

    public Message(User sender, Chat chat, String text) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
    }

    public Message() {}

    public Long getId() {
        return id;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
