package com.nexus.abstraction;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
public abstract class AbstractAppUser implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private User user;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdDate = Instant.now();
    @Column(nullable = false)
    private boolean archived = false;

    public AbstractAppUser(User user) {
        this.user = user;
    }
    public AbstractAppUser() {}

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
