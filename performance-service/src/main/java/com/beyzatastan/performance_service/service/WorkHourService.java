package com.beyzatastan.performance_service.service;

import com.beyzatastan.performance_service.dto.request.WorkHourRequest;
import com.beyzatastan.performance_service.dto.response.WorkHourResponse;
import com.beyzatastan.performance_service.entity.WorkHour;
import com.beyzatastan.performance_service.entity.WorkHourStatus;
import com.beyzatastan.performance_service.mapper.PerformanceReviewMapper;
import com.beyzatastan.performance_service.mapper.WorkHourMapper;
import com.beyzatastan.performance_service.repository.WorkHourRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkHourService {

    private final WorkHourRepository repository;

    @Transactional
    public WorkHourResponse create(WorkHourRequest request) {
        // aynı employee_id + date varsa update mantığı da seçebilirdin
        repository.findByEmployeeIdAndWorkDate(request.getEmployeeId(), request.getWorkDate())
                .ifPresent(existing -> {
                    throw new IllegalStateException("WorkHour already exists for employee/date");
                });

        WorkHour entity = WorkHourMapper.toEntity(request);

        if (entity.getStatus() == null) entity.setStatus(WorkHourStatus.PRESENT);
        entity.setTotalHours(calcTotalHours(entity));

        return WorkHourMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<WorkHourResponse> getAll() {
        return WorkHourMapper.toResponseList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public WorkHourResponse getById(Long id) {
        return WorkHourMapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<WorkHourResponse> getByEmployee(Long employeeId) {
        return WorkHourMapper.toResponseList(repository.findByEmployeeId(employeeId));
    }

    @Transactional
    public WorkHourResponse update(Long id, WorkHourRequest request) {
        WorkHour entity = find(id);
        WorkHourMapper.updateEntity(entity, request);

        if (entity.getStatus() == null) entity.setStatus(WorkHourStatus.PRESENT);
        entity.setTotalHours(calcTotalHours(entity));

        return WorkHourMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private WorkHour find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkHour not found: " + id));
    }

    private BigDecimal calcTotalHours(WorkHour wh) {
        if (wh.getCheckIn() == null || wh.getCheckOut() == null) return null;

        long minutes = Duration.between(wh.getCheckIn(), wh.getCheckOut()).toMinutes();
        if (minutes < 0) {
            throw new IllegalArgumentException("checkOut cannot be before checkIn");
        }

        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        return hours;
    }
}
