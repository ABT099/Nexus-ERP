package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void findAllByUser_shouldReturnNotifications() {
        // Arrange
        List<Notification> notifications = List.of(
                new Notification(),
                new Notification()
        );

        when(notificationRepository.findAllByUserId(1L)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.findAllByUserId(1L);

        // Assert & Verify
        assertIterableEquals(notifications, result);

        verify(notificationRepository).findAllByUserId(1L);
    }

    @Test
    void save_shouldSaveSingleNotification() {
        // Arrange
        CreateNotificationDto dto = new CreateNotificationDto(1L, "title", "body", NotificationType.REMINDER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        // Act
        notificationService.save(dto);

        // Verify
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void save_shouldThrowException_whenUserNotFound() {
        CreateNotificationDto dto = new CreateNotificationDto(1L, "title", "body", NotificationType.REMINDER);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.save(dto)
        );

        verifyNoInteractions(notificationRepository);
    }

    @Test
    void saveAll_shouldSaveMultipleNotifications() {
        List<CreateNotificationDto> dtos = List.of(
                new CreateNotificationDto(1L, "title1", "body1", NotificationType.REMINDER),
                new CreateNotificationDto(2L, "title2", "body2", NotificationType.REMINDER),
                new CreateNotificationDto(3L, "title3", "body3", NotificationType.REMINDER)
        );

        Set<Long> ids = new HashSet<>(List.of(1L, 2L, 3L));
        List<User> users = ids.stream().map(id -> {
            User user = new User();
            user.setId(id);
            return user;
        }).collect(Collectors.toList());

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(notificationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Notification> result = notificationService.saveAll(dtos);

        assertEquals(3, result.size());
        assertEquals(1L, result.getFirst().getUser().getId());
        assertEquals("title1", result.getFirst().getTitle());
        assertEquals("body1", result.getFirst().getBody());

        verify(userRepository).findAllById(ids);
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void saveAll_shouldThrowException_whenUserNotFound() {
        List<CreateNotificationDto> dtos = List.of(
                new CreateNotificationDto(1L, "title1", "body1", NotificationType.REMINDER),
                new CreateNotificationDto(2L, "title2", "body2", NotificationType.REMINDER)
        );

        Set<Long> ids = new HashSet<>(List.of(1L, 2L));
        User user = new User();
        user.setId(1L);
        List<User> users = List.of(user);
        when(userRepository.findAllById(ids)).thenReturn(List.of(user));

        when(userRepository.findAllById(ids)).thenReturn(users);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.saveAll(dtos));

        verify(userRepository).findAllById(ids);
        verify(notificationRepository, never()).saveAll(anyList());
    }

    @Test
    void readBatch_shouldMarkNotificationsAsRead() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        List<Notification> notifications = List.of(
                new Notification(),
                new Notification(),
                new Notification()
        );

        when(notificationRepository.findAllById(ids)).thenReturn(notifications);

        notificationService.readBatch(ids);

        for (Notification notification : notifications) {
            assertTrue(notification.isRead());
        }

        verify(notificationRepository).saveAll(notifications);
    }

    @Test
    void readBatch_shouldDoNothingWhenNoNotificationsFound() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        when(notificationRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        notificationService.readBatch(ids);

        verify(notificationRepository, never()).saveAll(anyList());
    }

    @Test
    void delete_shouldDeleteNotification() {
        notificationService.delete(1L);

        verify(notificationRepository).deleteById(1L);
    }
}
