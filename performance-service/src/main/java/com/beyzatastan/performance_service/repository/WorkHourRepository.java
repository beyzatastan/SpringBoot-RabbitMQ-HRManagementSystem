package com.beyzatastan.performance_service.repository;

import com.beyzatastan.performance_service.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {
    List<WorkHour> findByEmployeeId(Long employeeId);
    Optional<WorkHour> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    List<WorkHour> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT w FROM WorkHour w WHERE w.employeeId = :employeeId AND YEAR(w.workDate) = :year AND MONTH(w.workDate) = :month")
    List<WorkHour> findMonthlyWorkHours(@Param("employeeId") Long employeeId,
                                        @Param("year") int year,
                                        @Param("month") int month);
}