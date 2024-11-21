package com.nexus.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Entity
public class Chat extends AbstractPersistable<Long> {
    private String name;
    @OneToMany(
            mappedBy = "chat",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Message> messages;

    public Chat(String name) {
        this.name = name;
    }

    public Chat() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
    }
}
