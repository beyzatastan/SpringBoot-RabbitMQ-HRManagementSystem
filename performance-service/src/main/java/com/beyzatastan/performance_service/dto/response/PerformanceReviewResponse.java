package com.beyzatastan.performance_service.dto.response;

import com.beyzatastan.performance_service.entity.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PerformanceReviewResponse {
    private Long id;
    private Long employeeId;
    private Long reviewerId;
    private LocalDate reviewPeriodStart;
    private LocalDate reviewPeriodEnd;

    private BigDecimal overallRating;
    private ReviewStatus status;
    private String comments;

    private List<PerformanceCriteriaResponse> criteriaList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
