package com.erasm.core.dto.response;

public class DashboardResponse {

    private Double billablePercentage;
    private Double benchPercentage;
    private Double utilizationPercentage;
    private Double availablePercentage;
    private Double allocationPercentage;
    private Long projectCount;
    private Long benchEmployeesCount;
    private Long overallocatedEmployeesCount;

    public DashboardResponse() {
    }

    public DashboardResponse(Double billablePercentage, Double benchPercentage, Double utilizationPercentage, Double availablePercentage, Double allocationPercentage, Long projectCount, Long benchEmployeesCount, Long overallocatedEmployeesCount) {
        this.billablePercentage = billablePercentage;
        this.benchPercentage = benchPercentage;
        this.utilizationPercentage = utilizationPercentage;
        this.availablePercentage = availablePercentage;
        this.allocationPercentage = allocationPercentage;
        this.projectCount = projectCount;
        this.benchEmployeesCount = benchEmployeesCount;
        this.overallocatedEmployeesCount = overallocatedEmployeesCount;
    }

    public Double getBillablePercentage() {
        return billablePercentage;
    }

    public void setBillablePercentage(Double billablePercentage) {
        this.billablePercentage = billablePercentage;
    }

    public Double getBenchPercentage() {
        return benchPercentage;
    }

    public void setBenchPercentage(Double benchPercentage) {
        this.benchPercentage = benchPercentage;
    }

    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public Double getAvailablePercentage() {
        return availablePercentage;
    }

    public void setAvailablePercentage(Double availablePercentage) {
        this.availablePercentage = availablePercentage;
    }

    public Double getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(Double allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }

    public Long getBenchEmployeesCount() {
        return benchEmployeesCount;
    }

    public void setBenchEmployeesCount(Long benchEmployeesCount) {
        this.benchEmployeesCount = benchEmployeesCount;
    }

    public Long getOverallocatedEmployeesCount() {
        return overallocatedEmployeesCount;
    }

    public void setOverallocatedEmployeesCount(Long overallocatedEmployeesCount) {
        this.overallocatedEmployeesCount = overallocatedEmployeesCount;
    }
}
