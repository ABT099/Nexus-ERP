package com.nexus.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public Customer createCustomer(String name, String email, String phone) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .setPhone(phone).build();

        return Customer.create(params);
    }
}
