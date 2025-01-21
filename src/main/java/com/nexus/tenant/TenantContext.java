package com.nexus.tenant;

public class TenantContext {
    private static final ThreadLocal<String> tenantId = new ThreadLocal<>();

    public static String getTenantId() {
        return tenantId.get();
    }

    public static void setTenantId(String id) {
        tenantId.set(id);
    }

    public static void clear() {
        tenantId.remove();
    }
}
