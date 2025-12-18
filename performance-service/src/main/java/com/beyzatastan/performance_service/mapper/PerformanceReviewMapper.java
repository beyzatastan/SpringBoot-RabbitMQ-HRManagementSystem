package com.beyzatastan.performance_service.mapper;

import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.PerformanceCriteriaRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceCriteriaResponse;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.entity.PerformanceCriteria;
import com.beyzatastan.performance_service.entity.PerformanceReview;

import java.util.ArrayList;
import java.util.List;

public class PerformanceReviewMapper {

    public static PerformanceReview toEntity(CreatePerformanceReviewRequest request) {
        if (request == null) return null;

        return PerformanceReview.builder()
                .employeeId(request.getEmployeeId())
                .reviewerId(request.getReviewerId())
                .reviewPeriodStart(request.getReviewPeriodStart())
                .reviewPeriodEnd(request.getReviewPeriodEnd())
                .comments(request.getComments())
                .build();
    }

    public static PerformanceReviewResponse toResponse(PerformanceReview entity) {
        if (entity == null) return null;

        PerformanceReviewResponse response = new PerformanceReviewResponse();
        response.setId(entity.getId());
        response.setEmployeeId(entity.getEmployeeId());
        response.setReviewerId(entity.getReviewerId());
        response.setReviewPeriodStart(entity.getReviewPeriodStart());
        response.setReviewPeriodEnd(entity.getReviewPeriodEnd());
        response.setOverallRating(entity.getOverallRating());
        response.setStatus(entity.getStatus());
        response.setComments(entity.getComments());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getCriteriaList() != null) {
            List<PerformanceCriteriaResponse> criteriaResponses = new ArrayList<>();
            for (PerformanceCriteria criteria : entity.getCriteriaList()) {
                criteriaResponses.add(
                        PerformanceCriteriaMapper.toResponse(criteria)
                );
            }
            response.setCriteriaList(criteriaResponses);
        }

        return response;
    }

    public static List<PerformanceCriteria> toCriteriaEntityList(
            List<PerformanceCriteriaRequest> requestList,
            PerformanceReview review
    ) {
        List<PerformanceCriteria> criteriaList = new ArrayList<>();
        if (requestList == null) return criteriaList;

        for (PerformanceCriteriaRequest request : requestList) {
            PerformanceCriteria criteria =
                    PerformanceCriteriaMapper.toEntity(request);
            criteria.setReview(review);
            criteriaList.add(criteria);
        }
        return criteriaList;
    }
    public static List<PerformanceReviewResponse> toResponseList(
            List<PerformanceReview> entities
    ) {
        List<PerformanceReviewResponse> responses = new ArrayList<>();
        if (entities == null) return responses;

        for (PerformanceReview entity : entities) {
            responses.add(toResponse(entity));
        }
        return responses;
    }

    public static void updateEntity(PerformanceReview entity, UpdatePerformanceReviewRequest request
    ) {
        if (entity == null || request == null) return;

        if (request.getReviewPeriodStart() != null) {
            entity.setReviewPeriodStart(request.getReviewPeriodStart());
        }
        if (request.getReviewPeriodEnd() != null) {
            entity.setReviewPeriodEnd(request.getReviewPeriodEnd());
        }
        if (request.getComments() != null) {
            entity.setComments(request.getComments());
        }
    }
}
