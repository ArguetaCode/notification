package com.campusenroll.notification.service;

import com.campusenroll.notification.dto.PaymentEvent;
import com.campusenroll.notification.entity.Notification;
import com.campusenroll.notification.entity.NotificationStatus;
import com.campusenroll.notification.exception.ResourceNotFoundException;
import com.campusenroll.notification.repository.NotificationRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final String IDEMPOTENCY_PREFIX = "notification:event:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(10);

    private final NotificationRepository notificationRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public NotificationService(NotificationRepository notificationRepository, StringRedisTemplate stringRedisTemplate) {
        this.notificationRepository = notificationRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Optional<Notification> createFromPaymentEvent(PaymentEvent event) {
        String eventId = event.getEventId();
        String idempotencyKey = IDEMPOTENCY_PREFIX + eventId;

        Boolean written = stringRedisTemplate.opsForValue().setIfAbsent(idempotencyKey, "1", IDEMPOTENCY_TTL);
        if (Boolean.FALSE.equals(written)) {
            return Optional.empty();
        }

        Notification notification = new Notification();
        notification.setStudentId(event.getStudentId());
        notification.setEventId(eventId);
        notification.setEventType(event.getEventType());
        notification.setPaymentId(event.getPaymentId());
        notification.setEnrollmentId(event.getEnrollmentId());
        notification.setStatus(NotificationStatus.CREATED);
        notification.setCreatedAt(OffsetDateTime.now());

        if ("payment.approved".equals(event.getEventType())) {
            notification.setType("PAYMENT_APPROVED");
            notification.setMessage("Tu pago fue aprobado y la inscripción puede continuar.");
        } else if ("payment.failed".equals(event.getEventType())) {
            notification.setType("PAYMENT_FAILED");
            notification.setMessage("Tu pago falló. La inscripción no fue confirmada y el cupo debe liberarse.");
        } else {
            notification.setType("PAYMENT_EVENT");
            notification.setMessage("Se recibió un evento de pago.");
        }

        return Optional.of(notificationRepository.save(notification));
    }

    public List<Notification> findByStudentId(Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setStatus(NotificationStatus.READ);
        return notificationRepository.save(notification);
    }
}
