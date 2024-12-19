package com.nexus.customer;

import com.nexus.person.PersonRepository;

public interface CustomerRepository extends PersonRepository<Customer, Long> {
}
