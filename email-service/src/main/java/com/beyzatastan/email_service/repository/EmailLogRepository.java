package com.beyzatastan.email_service.repository;

import com.beyzatastan.email_service.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByStatus(String status);
    List<EmailLog> findByToEmail(String toEmail);
}
