package com.beyzatastan.performance_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerformanceCriteriaRequest {
    private String criteriaName;
    private BigDecimal rating;
    private BigDecimal maxRating;
    private String comments;
}
