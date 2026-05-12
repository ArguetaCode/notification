package com.campusenroll.notification.event;

import com.campusenroll.notification.dto.PaymentEvent;
import com.campusenroll.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private final NotificationService notificationService;

    public PaymentEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "campusenroll.notifications.payments")
    public void handlePaymentEvent(PaymentEvent event, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        event.setEventType(routingKey);
        notificationService.createFromPaymentEvent(event);
    }
}
