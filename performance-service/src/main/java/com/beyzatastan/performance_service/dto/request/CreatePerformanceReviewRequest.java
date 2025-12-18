package com.beyzatastan.performance_service.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePerformanceReviewRequest {
    private Long employeeId;
    private Long reviewerId;
    private LocalDate reviewPeriodStart;
    private LocalDate reviewPeriodEnd;
    private String comments;

    private List<PerformanceCriteriaRequest> criteriaList;
}
