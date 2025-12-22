package com.beyzatastan.email_service.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendWelcomeEmail(String to, String username);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendLeaveApprovalEmail(String to, String employeeName, String leaveType);
    void sendSalaryPaymentEmail(String to, String employeeName, String amount);
}