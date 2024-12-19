package com.nexus.abstraction;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class UserContext {

    protected Long getUserId() {
        Authentication authentication = getAuthentication();

        return Long.parseLong(authentication.getCredentials().toString());
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication;
    }

}
