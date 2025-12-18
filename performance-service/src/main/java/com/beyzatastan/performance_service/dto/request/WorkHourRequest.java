package com.beyzatastan.performance_service.dto.request;

import com.beyzatastan.performance_service.entity.WorkHourStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkHourRequest {
    private Long employeeId;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private WorkHourStatus status; // null ise PRESENT
    private String notes;
}
