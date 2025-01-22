package com.nexus.user;

import com.nexus.auth.AuthenticationService;
import com.nexus.auth.LoginRequest;
import com.nexus.exception.DuplicateResourceException;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationContext.class);
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final TenantRepository tenantRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserCreationContext(UserRepository userRepository, AuthenticationService authenticationService, TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.tenantRepository = tenantRepository;
    }

    public UserDTO create(String username, String password, UserType userType) {
        if (userRepository.existsByUsername(username)) {
            LOGGER.error("Username {} already exists", username);
            throw new DuplicateResourceException("Username already exists");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(password);

        Tenant tenant = new Tenant();
        tenantRepository.save(tenant);

        User user = new User(username, hashedPassword, userType, tenant.getId());
        userRepository.save(user);

        LOGGER.debug("Created user with username {}", username);

        String token  = authenticationService.getToken(new LoginRequest(username, password));

        return new UserDTO(user, token);
    }
}
