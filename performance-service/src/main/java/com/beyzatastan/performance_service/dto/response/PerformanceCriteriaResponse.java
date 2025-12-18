package com.beyzatastan.performance_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerformanceCriteriaResponse {
    private Long id;
    private String criteriaName;
    private BigDecimal rating;
    private BigDecimal maxRating;
    private String comments;
}