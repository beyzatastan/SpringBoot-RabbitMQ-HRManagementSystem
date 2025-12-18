package com.beyzatastan.performance_service.mapper;

import com.beyzatastan.performance_service.dto.request.WorkHourRequest;
import com.beyzatastan.performance_service.dto.response.WorkHourResponse;
import com.beyzatastan.performance_service.entity.WorkHour;
import com.beyzatastan.performance_service.entity.WorkHourStatus;

import java.util.ArrayList;
import java.util.List;

public class WorkHourMapper {

    public static WorkHour toEntity(WorkHourRequest request) {
        if (request == null) return null;

        return WorkHour.builder()
                .employeeId(request.getEmployeeId())
                .workDate(request.getWorkDate())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : WorkHourStatus.PRESENT
                )
                .notes(request.getNotes())
                .build();
    }

    public static void updateEntity(WorkHour entity, WorkHourRequest request) {
        if (entity == null || request == null) return;

        if (request.getCheckIn() != null) {
            entity.setCheckIn(request.getCheckIn());
        }
        if (request.getCheckOut() != null) {
            entity.setCheckOut(request.getCheckOut());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            entity.setNotes(request.getNotes());
        }
    }

    public static WorkHourResponse toResponse(WorkHour entity) {
        if (entity == null) return null;

        WorkHourResponse response = new WorkHourResponse();
        response.setId(entity.getId());
        response.setEmployeeId(entity.getEmployeeId());
        response.setWorkDate(entity.getWorkDate());
        response.setCheckIn(entity.getCheckIn());
        response.setCheckOut(entity.getCheckOut());
        response.setTotalHours(entity.getTotalHours());
        response.setStatus(entity.getStatus());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
    public static List<WorkHourResponse> toResponseList(
            List<WorkHour> entities
    ) {
        List<WorkHourResponse> responses = new ArrayList<>();
        if (entities == null) return responses;

        for (WorkHour entity : entities) {
            responses.add(toResponse(entity));
        }
        return responses;
    }

}
