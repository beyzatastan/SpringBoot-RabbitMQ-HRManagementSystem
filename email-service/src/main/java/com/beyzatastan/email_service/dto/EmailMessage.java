package com.beyzatastan.email_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMessage {

    private String to;
    private String subject;
    private String body;

    // Opsiyonel ama enterprise için faydalı
    private String type; // WELCOME, RESET_PASSWORD, SALARY, LEAVE_APPROVAL
}
