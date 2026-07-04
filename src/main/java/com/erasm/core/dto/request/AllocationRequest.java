package com.erasm.core.dto.request;

import com.erasm.core.enums.AllocationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AllocationRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Allocation percentage is required")
    @Min(value = 1, message = "Allocation percentage must be greater than 0")
    @Max(value = 100, message = "Allocation percentage cannot exceed 100")
    private Double allocationPercentage;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private AllocationStatus status;

    public AllocationRequest() {
    }

    public AllocationRequest(Long employeeId, Long projectId, Double allocationPercentage, LocalDate startDate, LocalDate endDate, AllocationStatus status) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.allocationPercentage = allocationPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Double getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(Double allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationStatus status) {
        this.status = status;
    }
}
