package com.nexus.admin;

import com.nexus.common.abstraction.AbstractPerson;
import com.nexus.event.Event;
import com.nexus.user.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Admin extends AbstractPerson {
    @ManyToMany(mappedBy = "admins")
    private List<Event> events;

    public Admin(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }
    public Admin() {
        super();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        if (!events.contains(event)) {
            events.add(event);
            event.addAdmin(this);
        }
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.removeAdmin(this);
    }
}
