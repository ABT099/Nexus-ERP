package com.nexus.zoned;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;

@Component
public class ZoneFilter extends OncePerRequestFilter {

    private static final String TIME_ZONE_COOKIE = "timeZone";
    private final TimeZoneHolder timeZoneHolder;

    public ZoneFilter(TimeZoneHolder timeZoneHolder) {
        this.timeZoneHolder = timeZoneHolder;
    }

    private final ThreadLocal<Boolean> applyFilter = ThreadLocal.withInitial(() -> false);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(TIME_ZONE_COOKIE))
                    .findFirst()
                    .ifPresent(cookie -> {
                        timeZoneHolder.setTimeZone(ZoneId.of(cookie.getValue()));
                    });
        }
        filterChain.doFilter(request, response);
    }

    public void setApplyFilter(boolean apply) {
        applyFilter.set(apply); }
}
