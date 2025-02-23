package com.nexus.tenant;

import com.nexus.stripe.StripeService;
import com.stripe.model.Customer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.nexus.admin.AdminService;


@RestController
@RequestMapping("tenants")
public class TenantController {

    private static final Logger LOG = LoggerFactory.getLogger(TenantController.class);
    private final StripeService stripeService;
    private final TenantRepository tenantRepository;
    private final AdminService adminService;

    public TenantController(TenantRepository tenantRepository, AdminService adminService, StripeService stripeService) {
        this.adminService = adminService;
        this.tenantRepository = tenantRepository;
        this.stripeService = stripeService;
    }

    @GetMapping
    public void getAll() {
    }

    @GetMapping("one")
    public void getOne() {
    }

    @Transactional
    @PostMapping
    public void create(@Valid @RequestBody TenantRequest request) {
        try {
            Customer customer = stripeService.createCustomer(request.name(), request.email(), request.phoneNumber());

            Tenant tenant = new Tenant(request.name(), request.email(), request.phoneNumber(), customer.getId());
            tenantRepository.save(tenant);

            if (request.adminAccounts() != null) {
                TenantContext.setTenantId(tenant.getId());
                adminService.batchSave(request.adminAccounts());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException("Failed to create tenant");
        }
    }


    @PatchMapping("change-stripe-account")
    public void changeStripeAccount() {
    }

    @PatchMapping("remove-stripe-account")
    public void removeStripeAccount() {
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        // delete the tenant and every thing related to it
    }
}
