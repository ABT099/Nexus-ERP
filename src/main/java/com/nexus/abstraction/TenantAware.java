package com.nexus.abstraction;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filters(@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId"))
public abstract class TenantAware extends AbstractPersistable<Long> {
    @Column(nullable = false)
    private UUID tenantId;

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
}
