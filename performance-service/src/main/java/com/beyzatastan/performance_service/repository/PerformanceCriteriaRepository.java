package com.beyzatastan.performance_service.repository;

import com.beyzatastan.performance_service.entity.PerformanceCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceCriteriaRepository extends JpaRepository<PerformanceCriteria, Long> {
    List<PerformanceCriteria> findByReviewId(Long reviewId);
}
