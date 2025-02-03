package com.nexus.tenant;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
public class Tenant {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false, insertable = false)
    private String id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdDate;

    public Tenant() {}

    public String getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

}
