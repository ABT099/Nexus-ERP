package com.nexus.chat;

import com.nexus.abstraction.TenantAware;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chat extends TenantAware {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(
            mappedBy = "chat",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Message> messages;

    public Chat(String tenantId) {
        setTenantId(tenantId);
    }

    public Chat() {}

    public Long getId() {
        return id;
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
