package com.nexus.common.abstraction;

import com.nexus.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractPerson extends AbstractAppUser {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    public AbstractPerson(User user, String firstName, String lastName) {
        super(user);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AbstractPerson() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
