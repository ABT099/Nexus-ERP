package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.common.Status;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Event extends AbstractAuditable<Admin, Integer> {
    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String name;
    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType type;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(nullable = false)
    private boolean urgent = false;

    @ManyToMany
    @JoinTable(
            name = "admins_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private final List<Admin> admins = new ArrayList<>();

    public Event(String name, String description, EventType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Event() {}

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

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Admin> getAdmins() {
        return admins;
    }


    public void addAdmin(Admin admin) {
        if (!admins.contains(admin)) {
            admins.add(admin);
            admin.addEvent(this);
        }
    }

    public void removeAdmin(Admin admin) {
        admins.remove(admin);
        admin.removeEvent(this);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}