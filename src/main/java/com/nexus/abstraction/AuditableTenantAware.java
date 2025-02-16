package com.nexus.abstraction;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@FilterDef(name = "ATenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filters(@Filter(name = "ATenantFilter", condition = "tenant_id = :tenantId"))
public abstract class AuditableTenantAware<ID extends Serializable> extends AbstractAppAuditing<ID> {
    @Column(nullable = false)
    private UUID tenantId;

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
}
