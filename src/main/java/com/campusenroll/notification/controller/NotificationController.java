package com.campusenroll.notification.controller;

import com.campusenroll.notification.entity.Notification;
import com.campusenroll.notification.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "notification-service",
                "status", "UP"
        );
    }

    @GetMapping("/students/{studentId}/notifications")
    public List<Notification> getByStudent(@PathVariable Long studentId) {
        return notificationService.findByStudentId(studentId);
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
