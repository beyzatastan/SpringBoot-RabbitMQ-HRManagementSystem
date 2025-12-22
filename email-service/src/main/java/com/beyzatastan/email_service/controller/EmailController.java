package com.beyzatastan.email_service.controller;

import com.beyzatastan.email_service.dto.EmailMessage;
import com.beyzatastan.email_service.entity.EmailLog;
import com.beyzatastan.email_service.repository.EmailLogRepository;
import com.beyzatastan.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailLogRepository emailLogRepository;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailMessage message) {
        emailService.sendEmail(message.getTo(), message.getSubject(), message.getBody());
        return ResponseEntity.ok("Email sent successfully!");
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(
            @RequestParam String email,
            @RequestParam String username) {
        emailService.sendWelcomeEmail(email, username);
        return ResponseEntity.ok("Welcome email sent!");
    }

    @GetMapping("/logs")
    public ResponseEntity<List<EmailLog>> getAllEmailLogs() {
        return ResponseEntity.ok(emailLogRepository.findAll());
    }

    @GetMapping("/logs/{status}")
    public ResponseEntity<List<EmailLog>> getEmailLogsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(emailLogRepository.findByStatus(status));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Email Service is working!");
    }
}