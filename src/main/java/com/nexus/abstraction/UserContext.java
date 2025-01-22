package com.nexus.abstraction;

import com.nexus.auth.jwt.AppAuthToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class UserContext {

    protected Long getUserId() {
        Authentication authentication = getAuthentication();

        if (authentication instanceof AppAuthToken) {
            return ((AppAuthToken) authentication).getUserId();
        }
        throw new RuntimeException("User ID not found in authentication details");
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication;
    }

}
