package com.nexus.tenant;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> tenantId = new ThreadLocal<>();

    public static UUID getTenantId() {
        return tenantId.get();
    }

    public static void setTenantId(UUID id) {
        tenantId.set(id);
    }

    public static void clear() {
        tenantId.remove();
    }
}
