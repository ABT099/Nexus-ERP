package com.nexus.config;

import com.nexus.tenant.TenantContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class TenantFilterConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", TenantContext.getTenantId());
        session.enableFilter("ATenantFilter").setParameter("tenantId", TenantContext.getTenantId());
    }
}