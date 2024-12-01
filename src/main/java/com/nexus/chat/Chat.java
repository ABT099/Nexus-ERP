package com.nexus.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Entity
public class Chat extends AbstractPersistable<Long> {
    @OneToMany(
            mappedBy = "chat",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Message> messages;

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
