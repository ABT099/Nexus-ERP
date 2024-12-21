package com.nexus.notification;

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

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Notification>> getAllByUserId(
            @Valid
            @Positive
            @PathVariable long id) {
        return ResponseEntity.ok(notificationRepository.findAllByUserId(id));
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
