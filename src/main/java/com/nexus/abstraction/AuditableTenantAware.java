package com.nexus.abstraction;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@MappedSuperclass
@FilterDef(name = "ATenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filters(@Filter(name = "ATenantFilter", condition = "tenant_id = :tenantId"))
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableTenantAware<ID extends Serializable> extends AbstractAppAuditing<ID> {
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
