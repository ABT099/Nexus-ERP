package com.nexus.admin;

import com.nexus.abstraction.AbstractPerson;
import com.nexus.event.Event;
import com.nexus.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Admin extends AbstractPerson {
    @ManyToMany(
            mappedBy = "admins",
            fetch = FetchType.LAZY
    )
    private final Set<Event> events = new HashSet<>();

    public Admin(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }
    public Admin() {
        super();
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }
}
