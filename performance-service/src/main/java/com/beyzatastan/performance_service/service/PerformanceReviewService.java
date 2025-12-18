package com.beyzatastan.performance_service.service;

import com.beyzatastan.performance_service.dto.request.CreatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.request.PerformanceCriteriaRequest;
import com.beyzatastan.performance_service.dto.request.UpdatePerformanceReviewRequest;
import com.beyzatastan.performance_service.dto.response.PerformanceReviewResponse;
import com.beyzatastan.performance_service.entity.PerformanceCriteria;
import com.beyzatastan.performance_service.entity.PerformanceReview;
import com.beyzatastan.performance_service.entity.ReviewStatus;
import com.beyzatastan.performance_service.mapper.PerformanceCriteriaMapper;
import com.beyzatastan.performance_service.mapper.PerformanceReviewMapper;
import com.beyzatastan.performance_service.repository.PerformanceReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;

    @Transactional
    public PerformanceReviewResponse create(CreatePerformanceReviewRequest request) {
        PerformanceReview review = PerformanceReviewMapper.toEntity(request);

        // criteria set + back-reference
        attachCriteria(review, request.getCriteriaList());

        // overall rating hesapla
        review.setOverallRating(calculateOverall(review.getCriteriaList()));

        PerformanceReview saved = reviewRepository.save(review);
        return PerformanceReviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getAll() {
        return PerformanceReviewMapper.toResponseList(reviewRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PerformanceReviewResponse getById(Long id) {
        return PerformanceReviewMapper.toResponse(findReview(id));
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getByEmployee(Long employeeId) {
        return PerformanceReviewMapper.toResponseList(reviewRepository.findByEmployeeId(employeeId));
    }

    @Transactional
    public PerformanceReviewResponse update(Long id, UpdatePerformanceReviewRequest request) {
        PerformanceReview review = findReview(id);

        // review update
        PerformanceReviewMapper.updateEntity(review, request);

        // criteria replace stratejisi: eskiyi sil, yeniyi ekle
        if (request.getCriteriaList() != null) {
            review.getCriteriaList().clear();
            attachCriteria(review, request.getCriteriaList());
            review.setOverallRating(calculateOverall(review.getCriteriaList()));
        }

        return PerformanceReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public PerformanceReviewResponse submit(Long id) {
        PerformanceReview review = findReview(id);
        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT reviews can be submitted");
        }
        review.setStatus(ReviewStatus.SUBMITTED);
        return PerformanceReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public PerformanceReviewResponse approve(Long id) {
        PerformanceReview review = findReview(id);
        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED reviews can be approved");
        }
        review.setStatus(ReviewStatus.APPROVED);
        return PerformanceReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public PerformanceReviewResponse reject(Long id) {
        PerformanceReview review = findReview(id);
        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED reviews can be rejected");
        }
        review.setStatus(ReviewStatus.REJECTED);
        return PerformanceReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.delete(findReview(id));
    }

    // ---- helpers ----

    private PerformanceReview findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PerformanceReview not found: " + id));
    }

    private void attachCriteria(PerformanceReview review, List<PerformanceCriteriaRequest> criteriaRequests) {
        if (criteriaRequests == null) return;

        for (PerformanceCriteriaRequest cReq : criteriaRequests) {
            PerformanceCriteria c =
                    PerformanceCriteriaMapper.toEntity(cReq);
            c.setReview(review);

            // null maxRating -> 5.00 default
            if (c.getMaxRating() == null) {
                c.setMaxRating(new BigDecimal("5.00"));
            }

            review.getCriteriaList().add(c);
        }
    }

    private BigDecimal calculateOverall(List<PerformanceCriteria> criteriaList) {
        if (criteriaList == null || criteriaList.isEmpty()) return null;

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (PerformanceCriteria c : criteriaList) {
            if (c.getRating() != null) {
                sum = sum.add(c.getRating());
                count++;
            }
        }
        if (count == 0) return null;

        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
