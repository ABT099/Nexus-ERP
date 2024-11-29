package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public List<Notification> findAllByUserId(long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    public void save(CreateNotificationDto createNotificationDto) {
        User user = userService.findById(createNotificationDto.userId());

        Notification notification = new Notification(
                user,
                createNotificationDto.title(),
                createNotificationDto.body(),
                createNotificationDto.type());

        notificationRepository.save(notification);
    }

    public List<Notification> saveAll(List<CreateNotificationDto> createNotificationDtos) {
        Set<Long> userIds = createNotificationDtos.stream()
                .map(CreateNotificationDto::userId)
                .collect(Collectors.toSet());

        Map<Long, User> usersMap = userService.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<Notification> notifications = new ArrayList<>(createNotificationDtos.size());
        for (CreateNotificationDto dto : createNotificationDtos) {
            User user = usersMap.get(dto.userId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found for ID: " + dto.userId());
            }
            notifications.add(new Notification(user, dto.title(), dto.body(), dto.type()));
        }

        return notificationRepository.saveAll(notifications);
    }

    public void readBatch(Set<Long> ids) {
        List<Notification> notifications = notificationRepository.findAllById(ids);

        if (notifications.isEmpty()) {
            return;
        }

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    public void delete(long id) {
        notificationRepository.deleteById(id);
    }
}
