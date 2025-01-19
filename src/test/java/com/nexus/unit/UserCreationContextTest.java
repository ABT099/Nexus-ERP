package com.nexus.unit;

import com.nexus.auth.AuthenticationService;
import com.nexus.auth.LoginRequest;
import com.nexus.exception.DuplicateResourceException;
import com.nexus.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserCreationContextTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private UserCreationContext userCreationContext;

    @Test
    void create_shouldCreateUser_whenUsernameIsUnique() {
        // Arrange
        String username = "test";
        String password = "test";
        UserType userType = UserType.SUPER_USER;

        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(authenticationService.getToken(loginRequest)).thenReturn("token");

        // Act
        UserDTO createdUser = userCreationContext.create(username, password, userType);

        // Assert
        assertNotNull(createdUser);
        assertEquals(username, createdUser.user().getUsername());
        assertTrue(new BCryptPasswordEncoder().matches(password, createdUser.user().getPassword()));
        assertEquals(userType, createdUser.user().getUserType());
        assertEquals("token", createdUser.token());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_shouldThrowException_whenUsernameIsNotUnique() {
        // Arrange
        String username = "test";
        String password = "test";
        UserType userType = UserType.SUPER_USER;

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> userCreationContext.create(username, password, userType));

        verify(userRepository, never()).save(any(User.class));
    }
}
