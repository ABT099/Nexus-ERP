package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<NotificationResponse>> getAllByUserId(@Valid @Positive @PathVariable long id) {
        return ResponseEntity.ok(
                notificationRepository.findAllByUserId(id).stream()
                        .map(notificationMapper::map)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<NotificationResponse> getById(@Valid @Positive @PathVariable long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Notification with id " + id + " not found")
                );

        return ResponseEntity.ok(notificationMapper.map(notification));
    }

    @GetMapping("/un-read-count/user/{id}")
    public ResponseEntity<Long> getUnreadCountByUserId(@Valid @Positive @PathVariable long id) {
        return ResponseEntity.ok(notificationRepository.countAllByUserIdAndRead(id, false));
    }

    @PatchMapping
    public void readBatch(@RequestBody Set<Long> ids) {
        List<Notification> notifications = notificationRepository.findAllById(ids);

        if (notifications.isEmpty()) {
            return;
        }

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }
}
