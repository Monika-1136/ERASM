package com.erasm.core.dto.response;

public class UtilizationReportResponse {

    private Long employeeId;
    private String employeeName;
    private String department;
    private Double totalAllocatedPercentage;
    private Double billablePercentage;
    private Double benchPercentage;

    public UtilizationReportResponse() {
    }

    public UtilizationReportResponse(Long employeeId, String employeeName, String department, Double totalAllocatedPercentage, Double billablePercentage, Double benchPercentage) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.totalAllocatedPercentage = totalAllocatedPercentage;
        this.billablePercentage = billablePercentage;
        this.benchPercentage = benchPercentage;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getTotalAllocatedPercentage() {
        return totalAllocatedPercentage;
    }

    public void setTotalAllocatedPercentage(Double totalAllocatedPercentage) {
        this.totalAllocatedPercentage = totalAllocatedPercentage;
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
}
