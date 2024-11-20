package com.nexus.customer;

import com.nexus.common.abstraction.AbstractPerson;
import com.nexus.user.User;
import jakarta.persistence.Entity;

@Entity
public class Customer extends AbstractPerson {
    public Customer(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }

    public Customer() {}
}
