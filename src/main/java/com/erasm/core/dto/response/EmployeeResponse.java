package com.erasm.core.dto.response;

import java.util.ArrayList;
import java.util.List;

public class EmployeeResponse {

    private Long employeeId;
    private Long userId;
    private String fullName;
    private String email;
    private String department;
    private String designation;
    private Double experienceYears;
    private Double totalAllocationPercentage;
    private Double availablePercentage;
    private List<String> projects = new ArrayList<>();
    private String availabilityStatus;
    private List<EmployeeSkillResponse> skills = new ArrayList<>();
    private List<CertificationResponse> certifications = new ArrayList<>();

    public EmployeeResponse() {
    }

    public EmployeeResponse(Long employeeId, Long userId, String fullName, String email, String department, String designation, Double experienceYears, Double totalAllocationPercentage) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.experienceYears = experienceYears;
        this.totalAllocationPercentage = totalAllocationPercentage;
        this.availablePercentage = 100.0 - (totalAllocationPercentage != null ? totalAllocationPercentage : 0.0);
        this.availabilityStatus = (totalAllocationPercentage != null && totalAllocationPercentage == 0.0) ? "AVAILABLE" : 
                                  ((totalAllocationPercentage != null && totalAllocationPercentage >= 100.0) ? "FULLY_ALLOCATED" : "PARTIALLY_ALLOCATED");
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Double getTotalAllocationPercentage() {
        return totalAllocationPercentage;
    }

    public void setTotalAllocationPercentage(Double totalAllocationPercentage) {
        this.totalAllocationPercentage = totalAllocationPercentage;
        this.availablePercentage = 100.0 - (totalAllocationPercentage != null ? totalAllocationPercentage : 0.0);
        this.availabilityStatus = (totalAllocationPercentage != null && totalAllocationPercentage == 0.0) ? "AVAILABLE" : 
                                  ((totalAllocationPercentage != null && totalAllocationPercentage >= 100.0) ? "FULLY_ALLOCATED" : "PARTIALLY_ALLOCATED");
    }

    public Double getAvailablePercentage() {
        return availablePercentage;
    }

    public void setAvailablePercentage(Double availablePercentage) {
        this.availablePercentage = availablePercentage;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public List<EmployeeSkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<EmployeeSkillResponse> skills) {
        this.skills = skills;
    }

    public List<CertificationResponse> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CertificationResponse> certifications) {
        this.certifications = certifications;
    }
}
