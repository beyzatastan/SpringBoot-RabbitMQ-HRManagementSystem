package com.beyzatastan.email_service.service;

import com.beyzatastan.email_service.entity.EmailLog;
import com.beyzatastan.email_service.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Override
    @Transactional
    public void sendEmail(String to, String subject, String body) {
        EmailLog emailLog = EmailLog.builder()
                .toEmail(to)
                .subject(subject)
                .body(body)
                .status("PENDING")
                .retryCount(0)
                .build();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@hrms.com");

            mailSender.send(message);

            emailLog.setStatus("SENT");
            emailLog.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            emailLog.setStatus("FAILED");
            emailLog.setErrorMessage(e.getMessage());
            emailLog.setRetryCount(emailLog.getRetryCount() + 1);
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw e; // RabbitMQ retry için exception fırlat

        } finally {
            emailLogRepository.save(emailLog);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Welcome to HRMS!";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Welcome to our HR Management System!\n\n" +
                        "Your account has been successfully created.\n\n" +
                        "Best regards,\n" +
                        "HRMS Team",
                username
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request";
        String body = String.format(
                "Hello,\n\n" +
                        "You have requested to reset your password.\n\n" +
                        "Your reset token is: %s\n\n" +
                        "This token will expire in 15 minutes.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "HRMS Team",
                resetToken
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendLeaveApprovalEmail(String to, String employeeName, String leaveType) {
        String subject = "Leave Request Approved";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Your %s leave request has been approved.\n\n" +
                        "Best regards,\n" +
                        "HRMS Team",
                employeeName, leaveType
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendSalaryPaymentEmail(String to, String employeeName, String amount) {
        String subject = "Salary Payment Notification";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Your salary of %s has been processed.\n\n" +
                        "Best regards,\n" +
                        "HRMS Team",
                employeeName, amount
        );
        sendEmail(to, subject, body);
    }
}