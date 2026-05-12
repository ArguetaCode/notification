package com.campusenroll.notification.config;

import com.campusenroll.notification.event.PaymentRoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PAYMENTS_EXCHANGE = "campusenroll.payments";
    public static final String NOTIFICATION_PAYMENTS_QUEUE = "campusenroll.notifications.payments";

    @Bean
    public DirectExchange paymentsExchange() {
        return new DirectExchange(PAYMENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationPaymentsQueue() {
        return new Queue(NOTIFICATION_PAYMENTS_QUEUE, true);
    }

    @Bean
    public Binding paymentApprovedBinding(Queue notificationPaymentsQueue, DirectExchange paymentsExchange) {
        return BindingBuilder.bind(notificationPaymentsQueue)
                .to(paymentsExchange)
                .with(PaymentRoutingKeys.APPROVED);
    }

    @Bean
    public Binding paymentFailedBinding(Queue notificationPaymentsQueue, DirectExchange paymentsExchange) {
        return BindingBuilder.bind(notificationPaymentsQueue)
                .to(paymentsExchange)
                .with(PaymentRoutingKeys.FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
