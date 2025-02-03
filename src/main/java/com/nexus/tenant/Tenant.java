package com.nexus.tenant;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Tenant {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    private UUID id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdDate;

    public Tenant() {}

    public UUID getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

}
