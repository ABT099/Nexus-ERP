package com.nexus.common.abstraction;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractUserService {

    protected String getUsername() {
        Authentication authentication = getAuthentication();

        String username = authentication.getName();

        if (username == null) {
            throw new RuntimeException("Username is null");
        }

        return username;
    }

    protected Long getUserId() {
        Authentication authentication = getAuthentication();

        Long userId = (Long) authentication.getCredentials();

        if (userId == null) {
            throw new RuntimeException("Username is null");
        }

        return userId;
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication;
    }

}
