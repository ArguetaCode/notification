package com.campusenroll.notification.repository;

import com.campusenroll.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
