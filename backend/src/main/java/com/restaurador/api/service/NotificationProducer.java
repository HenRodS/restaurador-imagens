package com.restaurador.api.service;

import com.restaurador.api.config.RabbitMQConfig;
import com.restaurador.api.dto.EmailNotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification(String to, String subject, String body) {
        EmailNotificationEvent event = new EmailNotificationEvent(to, subject, body);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, event);
    }
}
