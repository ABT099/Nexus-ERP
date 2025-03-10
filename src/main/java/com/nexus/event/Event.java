package com.nexus.event;

import com.nexus.abstraction.AbstractAppAuditing;
import com.nexus.admin.Admin;
import com.nexus.common.Status;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Event extends AbstractAppAuditing<Long> {

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
    private Instant date;

    @Column(nullable = false)
    private boolean urgent = false;

    @Column(nullable = false)
    private boolean archived = false;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "admins_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private final Set<Admin> admins = new HashSet<>();

    public Event(String name, String description, EventType type, Instant date) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.date = date;
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

    public Set<Admin> getAdmins() {
        return admins;
    }


    public void addAdmin(Admin admin) {
        admins.add(admin);
        admin.addEvent(this);
    }

    public void removeAdmin(Admin admin) {
        admins.remove(admin);
        admin.removeEvent(this);
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}