package com.nexus.ineteraction;

import com.nexus.project.Project;
import com.nexus.projectstep.ProjectStep;
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

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "step_id")
    private ProjectStep step;

    public Interaction(User interactedBy, String title, String description, Instant interactionDate, Project project) {
        this.interactedBy = interactedBy;
        this.title = title;
        this.description = description;
        this.interactionDate = interactionDate;
    }

    public Interaction(User interactedBy, String title, String description, Instant interactionDate, ProjectStep step) {
        this.interactedBy = interactedBy;
        this.title = title;
        this.description = description;
        this.interactionDate = interactionDate;
    }

    public Interaction() {}

    public User getInteractedBy() {
        return interactedBy;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getInteractionDate() {
        return interactionDate;
    }
}
