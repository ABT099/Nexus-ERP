package com.nexus.stripe;

import com.nexus.income.Income;
import com.nexus.tenant.TenantContext;
import com.nexus.tenant.TenantRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    private final TenantRepository tenantRepository;

    public StripeService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Customer createCustomer(String name, String email, String phone) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .setPhone(phone).build();

        return Customer.create(params);
    }

    public Income createCharge(Income income) throws StripeException {
        String stripeAccountId = tenantRepository.getStripeAccountIdByTenantId(TenantContext.getTenantId());

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(income.getAmount())
                .setCurrency(income.getCurrency())
                .setSource(income.getSource())
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(stripeAccountId)
                .build();

        Charge charge = Charge.create(params, requestOptions);

        income.setChargeId(charge.getId());

        return income;
    }
}
