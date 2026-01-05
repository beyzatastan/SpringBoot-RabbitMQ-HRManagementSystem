package com.beyzatastan.performance_service.service;

import com.beyzatastan.performance_service.dto.request.WorkHourRequest;
import com.beyzatastan.performance_service.dto.response.WorkHourResponse;

import java.util.List;

public interface WorkHourService {
    void deleteWorkHour(Long id);
    WorkHourResponse checkIn(WorkHourRequest request);
    WorkHourResponse checkOut(Long id, WorkHourRequest request);
    WorkHourResponse updateWorkHour(Long id, WorkHourRequest request);
    List<WorkHourResponse> getMonthlyWorkHours(Long employeeId, int year, int month);
    List<WorkHourResponse> getByEmployeeId(Long employeeId);
    WorkHourResponse getById(Long id);

}
