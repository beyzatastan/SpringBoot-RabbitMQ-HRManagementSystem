package com.beyzatastan.performance_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "performance_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private PerformanceReview review;

    @Column(name = "criteria_name", nullable = false, length = 100)
    private String criteriaName;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "max_rating", precision = 3, scale = 2)
    private BigDecimal maxRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @PrePersist
    public void prePersist() {
        if (maxRating == null) {
            maxRating = new BigDecimal("5.00");
        }
    }
}
