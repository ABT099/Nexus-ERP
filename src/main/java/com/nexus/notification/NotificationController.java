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

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Notification>> getAllByUserId(
            @Valid
            @Positive
            @PathVariable long id) {
        return ResponseEntity.ok(notificationService.findAllByUserId(id));
    }

    @PatchMapping
    public void readBatch(@RequestBody Set<Long> ids) {
        notificationService.readBatch(ids);
    }
}
