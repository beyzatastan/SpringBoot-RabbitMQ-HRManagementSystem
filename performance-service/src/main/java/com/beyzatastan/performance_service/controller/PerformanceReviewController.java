package com.beyzatastan.performance_service.controller;

import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.entity.ReviewStatus;
import com.beyzatastan.performance_service.service.PerformanceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/reviews")
@RequiredArgsConstructor
@Slf4j
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    @PostMapping
    public ResponseEntity<PerformanceReviewResponse> createReview(
            @Valid @RequestBody CreatePerformanceReviewRequest request) {
        PerformanceReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePerformanceReviewRequest request) {
        PerformanceReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PerformanceReviewResponse> submitReview(@PathVariable Long id) {
        PerformanceReviewResponse response = reviewService.submitReview(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PerformanceReviewResponse> approveReview(@PathVariable Long id) {
        PerformanceReviewResponse response = reviewService.approveReview(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PerformanceReviewResponse> rejectReview(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        PerformanceReviewResponse response = reviewService.rejectReview(id, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReviewResponse> getReview(@PathVariable Long id) {
        PerformanceReviewResponse response = reviewService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceReviewResponse>> getByEmployee(
            @PathVariable Long employeeId) {
        List<PerformanceReviewResponse> response = reviewService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PerformanceReviewResponse>> getByStatus(
            @PathVariable ReviewStatus status) {
        List<PerformanceReviewResponse> response = reviewService.getByStatus(status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}