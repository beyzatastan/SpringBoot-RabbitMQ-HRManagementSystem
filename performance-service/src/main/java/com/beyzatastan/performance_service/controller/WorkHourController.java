package com.beyzatastan.performance_service.controller;

import com.beyzatastan.performance_service.dto.request.WorkHourRequest;
import com.beyzatastan.performance_service.dto.response.WorkHourResponse;
import com.beyzatastan.performance_service.service.WorkHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/workhours")
@RequiredArgsConstructor
@Slf4j
public class WorkHourController {

    private final WorkHourService workHourService;

    @PostMapping("/checkin")
    public ResponseEntity<WorkHourResponse> checkIn(
            @Valid @RequestBody WorkHourRequest request) {
        WorkHourResponse response = workHourService.checkIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<WorkHourResponse> checkOut(
            @PathVariable Long id,
            @Valid @RequestBody WorkHourRequest request) {
        WorkHourResponse response = workHourService.checkOut(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkHourResponse> updateWorkHour(
            @PathVariable Long id,
            @Valid @RequestBody WorkHourRequest request) {
        WorkHourResponse response = workHourService.updateWorkHour(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkHourResponse> getWorkHour(@PathVariable Long id) {
        WorkHourResponse response = workHourService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<WorkHourResponse>> getByEmployee(
            @PathVariable Long employeeId) {
        List<WorkHourResponse> response = workHourService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/monthly")
    public ResponseEntity<List<WorkHourResponse>> getMonthlyWorkHours(
            @PathVariable Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        List<WorkHourResponse> response = workHourService.getMonthlyWorkHours(employeeId, year, month);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkHour(@PathVariable Long id) {
        workHourService.deleteWorkHour(id);
        return ResponseEntity.noContent().build();
    }
}