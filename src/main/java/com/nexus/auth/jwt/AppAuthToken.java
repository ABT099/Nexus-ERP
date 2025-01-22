package com.nexus.auth.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class AppAuthToken extends UsernamePasswordAuthenticationToken {
    private final Long userId;
    private final String tenantId;

    public AppAuthToken(UserDetails principal, Long userId, String tenantId) {
        super(principal, null, principal.getAuthorities());
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
