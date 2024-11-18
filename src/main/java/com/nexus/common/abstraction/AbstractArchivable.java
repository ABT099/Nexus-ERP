package com.nexus.common.abstraction;

import com.nexus.auth.user.User;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.domain.AbstractAuditable;

@MappedSuperclass
public abstract class AbstractArchivable extends AbstractAuditable<User, Long> {
    private boolean archived;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
