package com.nexus.customer;

import com.nexus.utils.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements Mapper<Customer, CustomerResponse> {
    @Override
    public CustomerResponse map(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getUser().getAvatarUrl(),
                customer.getFirstName(),
                customer.getLastName()
        );
    }
}
