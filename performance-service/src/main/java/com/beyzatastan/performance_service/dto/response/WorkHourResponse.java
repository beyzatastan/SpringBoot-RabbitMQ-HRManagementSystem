package com.beyzatastan.performance_service.dto.response;

import com.beyzatastan.performance_service.entity.WorkHourStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class WorkHourResponse {
    private Long id;
    private Long employeeId;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal totalHours;
    private WorkHourStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
