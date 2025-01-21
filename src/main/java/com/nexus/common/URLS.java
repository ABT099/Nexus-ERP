package com.nexus.common;

import org.springframework.http.HttpMethod;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class URLS {
    public static final String[] AllowedUrls = new String[] {
            "/auth/login",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    public static final Map<HttpMethod, String[]> AllowedMethodUrls = Map.of(
            HttpMethod.POST, new String[] { "/admins" }
    );

    public static final Set<String> URLSWithRelativePaths = new HashSet<>();
    public static final Set<String> URLSWithoutRelativePaths = new HashSet<>();


    static {
        configure();
    }

    public static void configure() {
        for (String url : AllowedUrls) {
            if (url.endsWith("**")) {
                String path = url.substring(0, url.length() - 2);
                URLSWithRelativePaths.add(path);
            } else {
                URLSWithoutRelativePaths.add(url);
            }
        }
    }
}

