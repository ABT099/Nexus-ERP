package com.nexus.abstraction;

import com.nexus.tenant.TenantContext;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filters(@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId"))
public abstract class TenantAware {
    private String tenantId;

    @PrePersist
    public void prePersist() {
        tenantId = TenantContext.getTenantId();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
