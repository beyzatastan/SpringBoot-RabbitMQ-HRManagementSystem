package com.beyzatastan.performance_service.service.impl;

import com.beyzatastan.performance_service.client.EmployeeServiceClient;
import com.beyzatastan.performance_service.dto.employee.EmployeeResponse;
import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.entity.PerformanceCriteria;
import com.beyzatastan.performance_service.entity.PerformanceReview;
import com.beyzatastan.performance_service.entity.ReviewStatus;
import com.beyzatastan.performance_service.mapper.PerformanceReviewMapper;
import com.beyzatastan.performance_service.repository.PerformanceReviewRepository;
import com.beyzatastan.performance_service.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeServiceClient employeeServiceClient;

    @Transactional
    public PerformanceReviewResponse createReview(CreatePerformanceReviewRequest request) {

        EmployeeResponse employee = employeeServiceClient.
                getEmployeeById(request.getEmployeeId())
                .getBody();

        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        PerformanceReview review = PerformanceReviewMapper.toEntity(request);
        review.setStatus(ReviewStatus.DRAFT);

        //kriterleri ekledik
        if (request.getCriteriaList() != null && !request.getCriteriaList().isEmpty()) {
            List<PerformanceCriteria> criteriaList =
                    PerformanceReviewMapper.toCriteriaEntityList(request.getCriteriaList(), review);
            review.setCriteriaList(criteriaList);

            // overall rating hesaplad
            BigDecimal totalRating = BigDecimal.ZERO;
            for (PerformanceCriteria criteria : criteriaList) {
                totalRating = totalRating.add(criteria.getRating());
            }
            BigDecimal avgRating = totalRating.divide(
                    new BigDecimal(criteriaList.size()), 2, RoundingMode.HALF_UP);
            review.setOverallRating(avgRating);
        }

        PerformanceReview saved = reviewRepository.save(review);
        log.info("Performance review created: id={}", saved.getId());

        return PerformanceReviewMapper.toResponse(saved);
    }

    @Transactional
    public PerformanceReviewResponse updateReview(Long id, UpdatePerformanceReviewRequest request) {

        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT reviews can be updated");
        }

        PerformanceReviewMapper.updateEntity(review, request);

        // kriterler güncellenmiş mi?
        if (request.getCriteriaList() != null && !request.getCriteriaList().isEmpty()) {
            review.getCriteriaList().clear();
            List<PerformanceCriteria> newCriteria =
                    PerformanceReviewMapper.toCriteriaEntityList(request.getCriteriaList(), review);
            review.setCriteriaList(newCriteria);

            // overall rating hesapla
            BigDecimal totalRating = BigDecimal.ZERO;
            for (PerformanceCriteria criteria : newCriteria) {
                totalRating = totalRating.add(criteria.getRating());
            }
            BigDecimal avgRating = totalRating.divide(
                    new BigDecimal(newCriteria.size()), 2, RoundingMode.HALF_UP);
            review.setOverallRating(avgRating);
        }

        PerformanceReview updated = reviewRepository.save(review);
        log.info("Performance review updated: {}", id);

        return PerformanceReviewMapper.toResponse(updated);
    }

    @Transactional
    public PerformanceReviewResponse submitReview(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT reviews can be submitted");
        }

        review.setStatus(ReviewStatus.SUBMITTED);
        PerformanceReview updated = reviewRepository.save(review);

        log.info("Review submitted: {}", id);
        return PerformanceReviewMapper.toResponse(updated);
    }

    @Transactional
    public PerformanceReviewResponse approveReview(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new RuntimeException("Only SUBMITTED reviews can be approved");
        }

        review.setStatus(ReviewStatus.APPROVED);
        PerformanceReview updated = reviewRepository.save(review);

        log.info("Review approved: {}", id);
        return PerformanceReviewMapper.toResponse(updated);
    }

    @Transactional
    public PerformanceReviewResponse rejectReview(Long id, String reason) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new RuntimeException("Only SUBMITTED reviews can be rejected");
        }

        review.setStatus(ReviewStatus.REJECTED);
        if (reason != null) {
            review.setComments(review.getComments() + "\nRejection reason: " + reason);
        }
        PerformanceReview updated = reviewRepository.save(review);

        log.info("Review rejected: {}", id);
        return PerformanceReviewMapper.toResponse(updated);
    }

    public PerformanceReviewResponse getById(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        return PerformanceReviewMapper.toResponse(review);
    }

    public List<PerformanceReviewResponse> getByEmployeeId(Long employeeId) {
        List<PerformanceReview> reviews = reviewRepository.findByEmployeeId(employeeId);
        return PerformanceReviewMapper.toResponseList(reviews);
    }

    public List<PerformanceReviewResponse> getByStatus(ReviewStatus status) {
        List<PerformanceReview> reviews = reviewRepository.findByStatus(status);
        return PerformanceReviewMapper.toResponseList(reviews);
    }

    @Transactional
    public void deleteReview(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (review.getStatus() == ReviewStatus.APPROVED) {
            throw new RuntimeException("Cannot delete approved reviews");
        }

        reviewRepository.delete(review);
    }
}
