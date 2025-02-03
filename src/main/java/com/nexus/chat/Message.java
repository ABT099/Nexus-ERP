package com.nexus.chat;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;

@Entity
public class Message extends AbstractPersistable<Long> {

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

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String messageText;

    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt = Instant.now();

    public Message(User sender, Chat chat, String messageText) {
        this.sender = sender;
        this.chat = chat;
        this.messageText = messageText;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String text) {
        this.messageText = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
