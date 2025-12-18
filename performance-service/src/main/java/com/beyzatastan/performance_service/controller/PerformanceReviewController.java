package com.beyzatastan.performance_service.controller;

import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    @PostMapping
    public PerformanceReviewResponse create(@RequestBody CreatePerformanceReviewRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<PerformanceReviewResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PerformanceReviewResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<PerformanceReviewResponse> getByEmployee(@PathVariable Long employeeId) {
        return service.getByEmployee(employeeId);
    }

    @PutMapping("/{id}")
    public PerformanceReviewResponse update(@PathVariable Long id,
                                            @RequestBody UpdatePerformanceReviewRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/submit")
    public PerformanceReviewResponse submit(@PathVariable Long id) {
        return service.submit(id);
    }

    @PutMapping("/{id}/approve")
    public PerformanceReviewResponse approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @PutMapping("/{id}/reject")
    public PerformanceReviewResponse reject(@PathVariable Long id) {
        return service.reject(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
