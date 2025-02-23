package com.nexus.tenant;

import com.nexus.stripe.SubscriptionStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TenantRepository extends CrudRepository<Tenant, UUID> {

    @Modifying
    @Query("""
    update Tenant t
    set t.subscriptionStatus = :newStatus
    where t.id = :tenantId
    """)
    void updateTenantSubscriptionStatus(UUID tenantId, SubscriptionStatus newStatus);

    @Query("""
    select t.stripeAccountId
    from Tenant t
    where t.id = :tenantId
    """)
    String getStripeAccountIdByTenantId(UUID tenantId);
}
