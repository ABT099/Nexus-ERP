package com.nexus.common.abstraction;

import com.nexus.user.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class AbstractAppUser extends AbstractArchivable {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AbstractAppUser(User user) {
        this.user = user;
    }
    public AbstractAppUser() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
