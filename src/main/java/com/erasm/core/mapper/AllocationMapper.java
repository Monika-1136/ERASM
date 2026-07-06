package com.erasm.core.mapper;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.entity.Allocation;
import org.springframework.stereotype.Component;

@Component
public class AllocationMapper {

    public AllocationResponse toResponse(Allocation allocation) {
        if (allocation == null) return null;
        AllocationResponse response = new AllocationResponse();
        response.setAllocationId(allocation.getAllocationId());
        if (allocation.getEmployee() != null) {
            response.setEmployeeId(allocation.getEmployee().getEmployeeId());
            if (allocation.getEmployee().getUser() != null) {
                response.setEmployeeName(allocation.getEmployee().getUser().getFullName());
            }
        }
        if (allocation.getProject() != null) {
            response.setProjectId(allocation.getProject().getProjectId());
            response.setProjectName(allocation.getProject().getProjectName());
        }
        response.setAllocationPercentage(allocation.getAllocationPercentage());
        response.setStartDate(allocation.getStartDate());
        response.setEndDate(allocation.getEndDate());
        response.setStatus(allocation.getStatus());
        return response;
    }
}
