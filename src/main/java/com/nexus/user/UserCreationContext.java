package com.nexus.user;

import com.nexus.auth.AuthenticationService;
import com.nexus.auth.LoginRequest;
import com.nexus.exception.DuplicateResourceException;
import com.nexus.tenant.TenantContext;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationContext.class);
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserCreationContext(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public UserDTO create(String username, String password, UserType userType) {
        if (userRepository.existsByUsername(username)) {
            LOGGER.error("Username {} already exists", username);
            throw new DuplicateResourceException("Username already exists");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(password);

        User user = new User(username, hashedPassword, userType, TenantContext.getTenantId());
        userRepository.save(user);

        LOGGER.debug("Created user with username {}", username);

        String token  = authenticationService.getToken(new LoginRequest(username, password));

        return new UserDTO(user, token);
    }

    public List<User> batchCreate(List<CreateUserDTO> createUserDTOs) {
        List<User> users = new ArrayList<>();

        for (CreateUserDTO createUserDTO : createUserDTOs) {
            if (userRepository.existsByUsername(createUserDTO.username())) {
                LOGGER.error("Username {} already exists", createUserDTO.username());
                throw new DuplicateResourceException("Username already exists");
            }

            String hashedPassword = bCryptPasswordEncoder.encode(createUserDTO.password());

            User user = new User(createUserDTO.username(), hashedPassword, createUserDTO.userType(), TenantContext.getTenantId());
            users.add(user);
        }

        userRepository.saveAll(users);
        LOGGER.debug("Created {} users", users.size());

        return users;
    }
}
