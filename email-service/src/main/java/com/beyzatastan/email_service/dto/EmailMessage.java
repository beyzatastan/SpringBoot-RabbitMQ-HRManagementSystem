package com.beyzatastan.email_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
    private String body;
}