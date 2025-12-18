package com.beyzatastan.performance_service.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdatePerformanceReviewRequest {
    private LocalDate reviewPeriodStart;
    private LocalDate reviewPeriodEnd;
    private String comments;

    private List<PerformanceCriteriaRequest> criteriaList; // replace strategy
}
