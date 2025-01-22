package com.nexus.unit;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserService;
import com.nexus.user.UserTenantDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        User user = userService.findById(1L);

        assertNotNull(user);
        verify(userRepository, atMostOnce()).findById(1L);
    }

    @Test
    void findById_shouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));

        User user = userService.findByUsername("admin");

        assertNotNull(user);
        verify(userRepository).findByUsername("admin");
    }

    @Test
    void findByUsername_shouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("admin"));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAllById_shouldReturnAllUsersWithTheIds() {
        when(userRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(new User(), new User(), new User()));

        List<User> users = userService.findAllById(List.of(1L, 2L, 3L));


        assertNotNull(users);
        assertEquals(3, users.size());
        verify(userRepository).findAllById(List.of(1L, 2L, 3L));
    }
}