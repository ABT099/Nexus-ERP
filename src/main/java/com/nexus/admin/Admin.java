package com.nexus.admin;

import com.nexus.common.abstraction.AbstractPerson;
import com.nexus.auth.user.User;
import jakarta.persistence.Entity;

@Entity
public class Admin extends AbstractPerson {
    public Admin(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }
    public Admin() {
        super();
    }
}
