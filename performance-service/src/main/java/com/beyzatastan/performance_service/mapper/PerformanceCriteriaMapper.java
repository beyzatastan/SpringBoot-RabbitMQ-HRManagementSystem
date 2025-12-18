package com.beyzatastan.performance_service.mapper;

import com.beyzatastan.performance_service.dto.request.PerformanceCriteriaRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceCriteriaResponse;
import com.beyzatastan.performance_service.entity.PerformanceCriteria;

import java.math.BigDecimal;

public class PerformanceCriteriaMapper {

    public static PerformanceCriteria toEntity(PerformanceCriteriaRequest request) {
        if (request == null) return null;

        return PerformanceCriteria.builder()
                .criteriaName(request.getCriteriaName())
                .rating(request.getRating())
                .maxRating(
                        request.getMaxRating() != null
                                ? request.getMaxRating()
                                : new BigDecimal("5.00")
                )
                .comments(request.getComments())
                .build();
    }

    public static PerformanceCriteriaResponse toResponse(PerformanceCriteria entity) {
        if (entity == null) return null;

        PerformanceCriteriaResponse response = new PerformanceCriteriaResponse();
        response.setId(entity.getId());
        response.setCriteriaName(entity.getCriteriaName());
        response.setRating(entity.getRating());
        response.setMaxRating(entity.getMaxRating());
        response.setComments(entity.getComments());
        return response;
    }
}
