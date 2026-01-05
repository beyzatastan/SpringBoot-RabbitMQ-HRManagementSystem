package com.beyzatastan.performance_service.service;

import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.entity.ReviewStatus;

import java.util.List;


public interface PerformanceReviewService  {
    PerformanceReviewResponse submitReview(Long id);
    PerformanceReviewResponse approveReview(Long id);
    PerformanceReviewResponse rejectReview(Long id, String reason);
     PerformanceReviewResponse getById(Long id);
    List<PerformanceReviewResponse> getByEmployeeId(Long employeeId);
    List<PerformanceReviewResponse> getByStatus(ReviewStatus status);
    void deleteReview(Long id);
    PerformanceReviewResponse updateReview(Long id, UpdatePerformanceReviewRequest request);
    PerformanceReviewResponse createReview(CreatePerformanceReviewRequest request);
}

