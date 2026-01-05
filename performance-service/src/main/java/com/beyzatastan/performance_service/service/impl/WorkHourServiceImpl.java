package com.beyzatastan.performance_service.service.impl;

import com.beyzatastan.performance_service.client.EmployeeServiceClient;
import com.beyzatastan.performance_service.dto.employee.EmployeeResponse;
import com.beyzatastan.performance_service.dto.request.WorkHourRequest;
import com.beyzatastan.performance_service.dto.response.WorkHourResponse;
import com.beyzatastan.performance_service.entity.WorkHour;
import com.beyzatastan.performance_service.mapper.WorkHourMapper;
import com.beyzatastan.performance_service.repository.WorkHourRepository;
import com.beyzatastan.performance_service.service.WorkHourService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkHourServiceImpl implements WorkHourService {

    private final WorkHourRepository workHourRepository;
    private final EmployeeServiceClient employeeServiceClient;

    @Transactional
    public WorkHourResponse checkIn(WorkHourRequest request) {
        EmployeeResponse employee = employeeServiceClient.getEmployeeById(request.getEmployeeId())
                .getBody();

        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        // aynı gün için kayıt var mı kontrol
        if (workHourRepository.existsByEmployeeIdAndWorkDate(
                request.getEmployeeId(), request.getWorkDate())) {
            throw new RuntimeException("Work hour record already exists for this date");
        }

        WorkHour workHour = WorkHourMapper.toEntity(request);
        workHour.setWorkDate(request.getWorkDate() != null ? request.getWorkDate() : LocalDate.now());

        WorkHour saved = workHourRepository.save(workHour);
        log.info("Check-in recorded: id={}", saved.getId());

        return WorkHourMapper.toResponse(saved);
    }

    @Transactional
    public WorkHourResponse checkOut(Long id, WorkHourRequest request) {
        WorkHour workHour = workHourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work hour record not found"));

        if (workHour.getCheckOut() != null) {
            throw new RuntimeException("Already checked out");
        }

        workHour.setCheckOut(request.getCheckOut());

        // total hours hesapla
        if (workHour.getCheckIn() != null && workHour.getCheckOut() != null) {
            Duration duration = Duration.between(workHour.getCheckIn(), workHour.getCheckOut());
            BigDecimal hours = BigDecimal.valueOf(duration.toMinutes() / 60.0)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            workHour.setTotalHours(hours);
        }

        WorkHour updated = workHourRepository.save(workHour);
        log.info("Check-out recorded: total hours={}", updated.getTotalHours());

        return WorkHourMapper.toResponse(updated);
    }

    @Transactional
    public WorkHourResponse updateWorkHour(Long id, WorkHourRequest request) {
        WorkHour workHour = workHourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work hour record not found"));

        WorkHourMapper.updateEntity(workHour, request);

        // total hours yeniden hesapla
        if (workHour.getCheckIn() != null && workHour.getCheckOut() != null) {
            Duration duration = Duration.between(workHour.getCheckIn(), workHour.getCheckOut());
            BigDecimal hours = BigDecimal.valueOf(duration.toMinutes() / 60.0)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            workHour.setTotalHours(hours);
        }

        WorkHour updated = workHourRepository.save(workHour);
        log.info("Work hour updated: {}", id);

        return WorkHourMapper.toResponse(updated);
    }

    public WorkHourResponse getById(Long id) {
        log.info("Getting work hour: {}", id);

        WorkHour workHour = workHourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work hour record not found"));

        return WorkHourMapper.toResponse(workHour);
    }

    public List<WorkHourResponse> getByEmployeeId(Long employeeId) {

        List<WorkHour> workHours = workHourRepository.findByEmployeeId(employeeId);
        return WorkHourMapper.toResponseList(workHours);
    }

    public List<WorkHourResponse> getMonthlyWorkHours(Long employeeId, int year, int month) {

        List<WorkHour> workHours = workHourRepository.findMonthlyWorkHours(employeeId, year, month);
        return WorkHourMapper.toResponseList(workHours);
    }

    @Transactional
    public void deleteWorkHour(Long id) {
        WorkHour workHour = workHourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work hour record not found"));

        workHourRepository.delete(workHour);
        log.info("Work hour deleted: {}", id);
    }
}