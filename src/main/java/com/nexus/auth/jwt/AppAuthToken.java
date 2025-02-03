package com.nexus.auth.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public class AppAuthToken extends UsernamePasswordAuthenticationToken {
    private final Long userId;
    private final UUID tenantId;

    public AppAuthToken(UserDetails principal, Long userId, UUID tenantId) {
        super(principal, null, principal.getAuthorities());
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }
}
