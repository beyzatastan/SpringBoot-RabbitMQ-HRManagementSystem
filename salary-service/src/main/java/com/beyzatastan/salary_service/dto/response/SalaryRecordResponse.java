package com.beyzatastan.salary_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRecordResponse {
    private Long id;
    private Long employeeId;
    private String employeeName; // feignden gelecek
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private LocalDate paymentDate;
    private Integer paymentMonth;
    private Integer paymentYear;
    private String status;
    private LocalDateTime createdAt;
}