package com.nexus.chat;

import com.nexus.abstraction.TenantAware;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chat extends TenantAware {

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
}
