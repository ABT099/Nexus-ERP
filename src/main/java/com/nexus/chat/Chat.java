package com.nexus.chat;

import com.nexus.abstraction.TenantAware;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Chat extends TenantAware {

    @OneToMany(
            mappedBy = "chat",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Message> messages;

    public Chat(UUID tenantId) {
        setTenantId(tenantId);
    }

    public Chat() {}
}
