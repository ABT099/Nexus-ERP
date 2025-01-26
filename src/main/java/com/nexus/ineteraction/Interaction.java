package com.nexus.ineteraction;

import com.nexus.user.User;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
public class Interaction {

    @Id
    @GeneratedValue
    private Long id;

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
    private ZonedDateTime interactionDate;
}
