package com.beyzatastan.salary_service.service.impl;

import com.beyzatastan.salary_service.client.EmployeeServiceClient;
import com.beyzatastan.salary_service.dto.employee.EmployeeResponse;
import com.beyzatastan.salary_service.dto.request.SalaryRecordRequest;
import com.beyzatastan.salary_service.dto.response.SalaryRecordResponse;
import com.beyzatastan.salary_service.entity.SalaryRecord;
import com.beyzatastan.salary_service.repository.SalaryRecordRepository;
import com.beyzatastan.salary_service.service.EmailProducer;
import com.beyzatastan.salary_service.service.SalaryRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryRecordServiceImpl implements SalaryRecordService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final EmailProducer emailProducer;

    @Transactional
    public SalaryRecordResponse createSalaryRecord(SalaryRecordRequest request) {
       //feign ile employee kontrolü
        EmployeeResponse employee = employeeServiceClient.getEmployeeById(request.getEmployeeId())
                .getBody();

        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        // aynı ay için kayıt var mı
        if (salaryRecordRepository.existsByEmployeeIdAndPaymentMonthAndPaymentYear(
                request.getEmployeeId(), request.getPaymentMonth(), request.getPaymentYear())) {
            throw new RuntimeException("Salary record already exists for this period");
        }

        // maaşı hesapla
        BigDecimal deductions = request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO;
        BigDecimal netSalary = request.getBaseSalary().subtract(deductions);

        // payment date - eğer boşsa ayın son günü
        LocalDate paymentDate = request.getPaymentDate() != null
                ? request.getPaymentDate()
                : LocalDate.of(request.getPaymentYear(), request.getPaymentMonth(), 1)
                .plusMonths(1).minusDays(1);

        SalaryRecord salaryRecord = SalaryRecord.builder()
                .employeeId(request.getEmployeeId())
                .baseSalary(request.getBaseSalary())
                .deductions(deductions)
                .netSalary(netSalary)
                .paymentDate(paymentDate)
                .paymentMonth(request.getPaymentMonth())
                .paymentYear(request.getPaymentYear())
                .status("PENDING")
                .build();

        SalaryRecord saved = salaryRecordRepository.save(salaryRecord);
        log.info("Salary record created: id={}", saved.getId());

        return mapToResponse(saved, employee);
    }

    @Transactional
    public SalaryRecordResponse paySalary(Long id) {
        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));

        if ("PAID".equals(salaryRecord.getStatus())) {
            throw new RuntimeException("Salary already paid");
        }

        EmployeeResponse employee = employeeServiceClient.getEmployeeById(salaryRecord.getEmployeeId()).getBody();

        salaryRecord.setStatus("PAID");
        salaryRecord.setPaymentDate(LocalDate.now());
        SalaryRecord updated = salaryRecordRepository.save(salaryRecord);

        if (employee != null) {
            try {
                String employeeName = employee.getFirstName() + " " + employee.getLastName();
                emailProducer.sendSalaryPaymentEmail(
                        employee.getEmail(),
                        employeeName,
                        updated.getNetSalary(),
                        updated.getPaymentMonth().toString(),
                        updated.getPaymentYear().toString()
                );
                log.info("Salary payment email queued");
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage());
            }
        }

        return mapToResponse(updated, employee);
    }

    public SalaryRecordResponse getById(Long id) {
        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));

        EmployeeResponse employee = employeeServiceClient.getEmployeeById(salaryRecord.getEmployeeId())
                .getBody();

        return mapToResponse(salaryRecord, employee);
    }

    public List<SalaryRecordResponse> getByEmployeeId(Long employeeId) {
        EmployeeResponse employee = employeeServiceClient.getEmployeeById(employeeId)
                .getBody();

        return salaryRecordRepository.findByEmployeeId(employeeId).stream()
                .map(record -> mapToResponse(record, employee))
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteSalaryRecord(Long id) {

        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));

        if ("PAID".equals(salaryRecord.getStatus())) {
            throw new RuntimeException("Cannot delete paid salary record");
        }

        salaryRecordRepository.delete(salaryRecord);
        log.info("Salary record deleted: {}", id);
    }

    private SalaryRecordResponse mapToResponse(SalaryRecord record, EmployeeResponse employee) {
        String employeeName = employee != null
                ? employee.getFirstName() + " " + employee.getLastName()
                : "Unknown";

        return SalaryRecordResponse.builder()
                .id(record.getId())
                .employeeId(record.getEmployeeId())
                .employeeName(employeeName)
                .baseSalary(record.getBaseSalary())
                .bonus(record.getBonus())
                .deductions(record.getDeductions())
                .netSalary(record.getNetSalary())
                .paymentDate(record.getPaymentDate())
                .paymentMonth(record.getPaymentMonth())
                .paymentYear(record.getPaymentYear())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}