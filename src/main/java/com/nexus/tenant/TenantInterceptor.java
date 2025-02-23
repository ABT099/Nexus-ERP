package com.nexus.tenant;

import com.nexus.auth.jwt.AppAuthToken;
import com.nexus.auth.URLS;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantRepository tenantRepository;

    public TenantInterceptor(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        // Check if the request method and URI are in AllowedMethodUrls
        if (URLS.AllowedMethodUrls.entrySet().stream()
                .anyMatch(entry -> entry.getKey().name().equals(requestMethod)
                        && Arrays.asList(entry.getValue()).contains(requestUri))) {
            return true;
        }

        // Check if the URI is in AllowedUrls
        for (String path : URLS.URLSWithRelativePaths) {
            if (requestUri.contains(path)) {
                return true;
            }
        }

        if (URLS.URLSWithoutRelativePaths.contains(requestUri)) {
            return true;
        }

        // Validate the tenant ID header
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID tenantId = null;

        if (authentication instanceof AppAuthToken) {
            tenantId  = ((AppAuthToken) authentication).getTenantId();
        }

        if (tenantId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Tenant ID is missing");
            return false;
        }

        // Check if the tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Tenant not found");
            return false;
        }

        // Set the tenant context
        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();
    }
}
