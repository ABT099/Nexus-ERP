package com.nexus.ineteraction;

import com.nexus.user.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;

@Entity
public class Interaction extends AbstractPersistable<Long> {

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private User interactedBy;

    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Instant interactionDate;

    public Interaction(User interactedBy, String title, String description, Instant interactionDate) {
        this.interactedBy = interactedBy;
        this.title = title;
        this.description = description;
        this.interactionDate = interactionDate;
    }

    public Interaction() {}
}
