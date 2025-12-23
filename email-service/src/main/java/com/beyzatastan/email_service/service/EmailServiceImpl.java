package com.beyzatastan.email_service.service;

import com.beyzatastan.email_service.entity.EmailLog;
import com.beyzatastan.email_service.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Override
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

            throw e; // RabbitMQ retry için
        } finally {
            emailLogRepository.save(emailLog);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        sendEmail(
                to,
                "Welcome to HRMS!",
                "Hello " + username + ",\n\n" +
                        "Welcome to our HR Management System!\n\n" +
                        "Best regards,\nHRMS Team"
        );
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        sendEmail(
                to,
                "Password Reset Request",
                "Hello,\n\n" +
                        "Your reset token is: " + resetToken + "\n\n" +
                        "This token expires in 15 minutes.\n\n" +
                        "HRMS Team"
        );
    }

    @Override
    public void sendLeaveApprovalEmail(String to, String employeeName, String leaveType) {
        sendEmail(
                to,
                "Leave Request Approved",
                "Hello " + employeeName + ",\n\n" +
                        "Your " + leaveType + " leave request has been approved.\n\n" +
                        "HRMS Team"
        );
    }

    @Override
    public void sendSalaryPaymentEmail(String to, String employeeName, String amount) {
        sendEmail(
                to,
                "Salary Payment Notification",
                "Hello " + employeeName + ",\n\n" +
                        "Your salary of " + amount + " has been processed.\n\n" +
                        "HRMS Team"
        );
    }
}
