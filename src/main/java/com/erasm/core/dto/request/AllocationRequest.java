package com.erasm.core.dto.request;

import com.erasm.core.enums.AllocationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request body for creating or updating an employee project allocation")
public class AllocationRequest {

    @Schema(description = "ID of the employee being allocated", example = "1")
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @Schema(description = "ID of the project the employee is allocated to", example = "1")
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @Schema(description = "ID of the optional resource request this allocation fulfills", example = "1")
    private Long resourceRequestId;

    @Schema(description = "Allocation percentage (1 to 100)", example = "50")
    @NotNull(message = "Allocation percentage is required")
    @Min(value = 1, message = "Allocation percentage must be greater than 0")
    @Max(value = 100, message = "Allocation percentage cannot exceed 100")
    private Double allocationPercentage;

    @Schema(description = "Start date of the allocation", example = "2026-07-01")
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "End date of the allocation (optional)", example = "2026-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Initial status of the allocation", allowableValues = {"ALLOCATED", "ACTIVE", "COMPLETED", "RELEASED"}, example = "ACTIVE")
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

    public AllocationRequest(Long employeeId, Long projectId, Long resourceRequestId, Double allocationPercentage, LocalDate startDate, LocalDate endDate, AllocationStatus status) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.resourceRequestId = resourceRequestId;
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

    public Long getResourceRequestId() {
        return resourceRequestId;
    }

    public void setResourceRequestId(Long resourceRequestId) {
        this.resourceRequestId = resourceRequestId;
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
