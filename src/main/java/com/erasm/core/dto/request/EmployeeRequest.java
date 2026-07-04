package com.erasm.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class EmployeeRequest {

    private Long userId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Designation is required")
    private String designation;

    @PositiveOrZero(message = "Experience years must be positive or zero")
    private Double experienceYears;

    public EmployeeRequest() {
    }

    public EmployeeRequest(Long userId, String department, String designation, Double experienceYears) {
        this.userId = userId;
        this.department = department;
        this.designation = designation;
        this.experienceYears = experienceYears;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Double experienceYears) {
        this.experienceYears = experienceYears;
    }
}
