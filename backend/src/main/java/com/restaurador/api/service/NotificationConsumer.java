package com.restaurador.api.service;

import com.restaurador.api.config.RabbitMQConfig;
import com.restaurador.api.dto.EmailNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    private final JavaMailSender mailSender;

    public NotificationConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveEmailNotification(EmailNotificationEvent event) {
        logger.info("Consumindo evento de notificação para: {}", event.getTo());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nao-responda@restaurador.com");
            message.setTo(event.getTo());
            message.setSubject(event.getSubject());
            message.setText(event.getBody());

            mailSender.send(message);
            logger.info("E-mail disparado com sucesso para: {}", event.getTo());
        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail para: {}", event.getTo(), e);
            // Em produção configuraríamos uma Dead Letter Queue (DLQ) para re-tentativas.
        }
    }
}
