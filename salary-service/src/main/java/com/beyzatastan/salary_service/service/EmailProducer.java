package com.beyzatastan.salary_service.service;

import com.beyzatastan.salary_service.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:mailExchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:mail.routing.key}")
    private String routingKey;

    public void sendEmail(EmailMessage message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            log.info("Email sent to queue successfully");
        } catch (Exception e) {
            log.error("Failed to send email to queue: {}", e.getMessage());
        }
    }

    public void sendSalaryPaymentEmail(String email, String employeeName,
                                       BigDecimal amount, String month, String year) {
        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Salary Payment Notification - " + month + "/" + year)
                .body(String.format(
                        "Hello %s,\n\n" +
                                "Your salary has been processed!\n\n" +
                                "Amount: $%s\n" +
                                "Period: %s/%s\n" +
                                "Status: Paid\n\n" +
                                "Best regards,\n" +
                                "HRMS Team",
                        employeeName, amount, month, year
                ))
                .type("SALARY_PAYMENT")
                .build();

        sendEmail(message);
    }
}