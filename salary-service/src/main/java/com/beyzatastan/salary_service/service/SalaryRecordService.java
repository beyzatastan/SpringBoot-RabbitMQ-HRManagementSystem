package com.beyzatastan.salary_service.service;

import com.beyzatastan.salary_service.dto.request.SalaryRecordRequest;
import com.beyzatastan.salary_service.dto.response.SalaryRecordResponse;

import java.util.List;

public interface SalaryRecordService {
    SalaryRecordResponse createSalaryRecord(SalaryRecordRequest request);
    SalaryRecordResponse paySalary(Long id);
    SalaryRecordResponse getById(Long id);

    List<SalaryRecordResponse> getByEmployeeId(Long employeeId);

    void deleteSalaryRecord(Long id);

}
