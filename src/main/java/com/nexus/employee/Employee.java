package com.nexus.employee;

import com.nexus.common.abstraction.AbstractPerson;
import com.nexus.user.User;
import jakarta.persistence.Entity;

@Entity
public class Employee extends AbstractPerson {
    public Employee(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }

    public Employee() {}
}
