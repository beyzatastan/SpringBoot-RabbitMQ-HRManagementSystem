package com.beyzatastan.salary_service.repository;

import com.beyzatastan.salary_service.entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

    List<SalaryRecord> findByEmployeeId(Long employeeId);

    List<SalaryRecord> findByPaymentYearAndPaymentMonth(Integer year, Integer month);

    boolean existsByEmployeeIdAndPaymentMonthAndPaymentYear(
            Long employeeId, Integer month, Integer year);
}