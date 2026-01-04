package com.beyzatastan.salary_service.controller;

import com.beyzatastan.salary_service.dto.request.SalaryRecordRequest;
import com.beyzatastan.salary_service.dto.response.SalaryRecordResponse;
import com.beyzatastan.salary_service.service.SalaryRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Slf4j
public class SalaryRecordController {

    private final SalaryRecordService salaryRecordService;

    @PostMapping
    public ResponseEntity<SalaryRecordResponse> createSalaryRecord(@Valid @RequestBody SalaryRecordRequest request) {

        SalaryRecordResponse response = salaryRecordService.createSalaryRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<SalaryRecordResponse> paySalary(@PathVariable Long id) {
        log.info("Pay the salary for id: {}", id);

        SalaryRecordResponse response = salaryRecordService.paySalary(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SalaryRecordResponse> getSalaryRecord(@PathVariable Long id) {
        log.info("Get salary record request received for id: {}", id);
        SalaryRecordResponse response = salaryRecordService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SalaryRecordResponse>> getSalariesByEmployee(@PathVariable Long employeeId) {

        List<SalaryRecordResponse> response = salaryRecordService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalaryRecord(@PathVariable Long id) {
        log.info("Delete salary record request received for id: {}", id);
        salaryRecordService.deleteSalaryRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Salary Service is working!");
    }
}