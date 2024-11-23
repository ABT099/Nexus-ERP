package com.nexus.user;

import com.nexus.chat.Chat;
import com.nexus.chat.ChatRepository;
import com.nexus.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;
    @InjectMocks
    private UserCreationService userCreationService;

    @Test
    void create_shouldCreateUserAndChat_whenUsernameIsUnique() {
        // Arrange
        String username = "test";
        String password = "test";
        UserType userType = UserType.SUPER_USER;

        when(userRepository.existsByUsername(username)).thenReturn(false);

        // Act
        User createdUser = userCreationService.create(username, password, userType);

        // Assert
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertTrue(new BCryptPasswordEncoder().matches(password, createdUser.getPassword()));
        assertEquals(userType, createdUser.getUserType());

        verify(userRepository, times(1)).save(any(User.class));
        verify(chatRepository, times(1)).save(any(Chat.class));
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
                () -> userCreationService.create(username, password, userType));

        verify(userRepository, never()).save(any(User.class));
        verify(chatRepository, never()).save(any(Chat.class));
    }
}