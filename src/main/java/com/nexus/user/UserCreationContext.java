package com.nexus.user;

import com.nexus.auth.AuthenticationService;
import com.nexus.auth.LoginRequest;
import com.nexus.exception.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserCreationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationContext.class);
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserCreationContext(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public UserDto create(String username, String password, UserType userType) {
        if (userRepository.existsByUsername(username)) {
            LOGGER.warn("Username {} already exists", username);
            throw new DuplicateResourceException("Username already exists");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(password);

        User user = new User(username, hashedPassword, userType);

        userRepository.save(user);
        LOGGER.debug("Created user with username {}", username);

        String token  = authenticationService.getToken(new LoginRequest(username, password));

        return new UserDto(user, token);
    }
}
