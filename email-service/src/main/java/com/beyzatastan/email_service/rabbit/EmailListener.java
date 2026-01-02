package com.beyzatastan.email_service.rabbit;

import com.beyzatastan.email_service.dto.EmailMessage;
import com.beyzatastan.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queue.name:mailQueue}")
    public void handleEmailMessage(EmailMessage message) {
        log.info("Received email message from queue: to={}, subject={}",
                message.getTo(), message.getSubject());

        try {
            emailService.sendEmail(message.getTo(), message.getSubject(), message.getBody());
            log.info("Email sent successfully to: {}", message.getTo());

        } catch (Exception e) {

        }
    }
}