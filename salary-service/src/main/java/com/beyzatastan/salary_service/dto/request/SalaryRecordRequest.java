package com.beyzatastan.salary_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRecordRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Base salary is required")
    private BigDecimal baseSalary;

    private BigDecimal deductions;
    @NotNull(message = "Payment month is required")

    private Integer paymentMonth;
    @NotNull(message = "Payment year is required")

    private Integer paymentYear;

    private LocalDate paymentDate;
}