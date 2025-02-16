package com.nexus.auth;

import com.nexus.auth.jwt.JwtService;
import com.nexus.user.UserService;
import com.nexus.user.UserTenantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public String getToken(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.username(),
                    request.password()
            ));
        } catch (AuthenticationException e) {
            LOG.error("Authentication failed for user: {}", request.username(), e);
            return null;
        }

        UserTenantDTO user = userService.findUserTenantInfo(request.username());
        return jwtService.generateToken(request.username(), user.id(), user.tenantId());
    }
}
