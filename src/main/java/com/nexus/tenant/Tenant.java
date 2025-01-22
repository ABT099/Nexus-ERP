package com.nexus.tenant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class Tenant {

    @Id
    private String id;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdDate;

    public String getId() {
        return id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public Tenant() {
        id = UUID.randomUUID().toString();
        createdDate = ZonedDateTime.now();
    }
}
