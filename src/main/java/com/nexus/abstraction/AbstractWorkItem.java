package com.nexus.abstraction;

import com.nexus.common.Status;
import com.nexus.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.ZonedDateTime;

@MappedSuperclass
public abstract class AbstractWorkItem extends AbstractAuditable<User, Integer> {
    @Column(nullable = false, columnDefinition = "text")
    private String name;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private ZonedDateTime startDate;
    @Column(nullable = false)
    private ZonedDateTime expectedEndDate;
    private ZonedDateTime actualEndDate;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public AbstractWorkItem(String name, String description, ZonedDateTime startDate, ZonedDateTime expectedEndDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.actualEndDate = actualEndDate;
    }

    public AbstractWorkItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(ZonedDateTime expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public ZonedDateTime getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(ZonedDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
