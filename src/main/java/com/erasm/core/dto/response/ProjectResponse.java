package com.erasm.core.dto.response;

import com.erasm.core.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectResponse {

    private Long projectId;
    private String projectName;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String technologyStack;
    private BigDecimal budget;
    private ProjectStatus projectStatus;

    public ProjectResponse() {
    }

    public ProjectResponse(Long projectId, String projectName, String clientName, LocalDate startDate, LocalDate endDate, String technologyStack, BigDecimal budget, ProjectStatus projectStatus) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.clientName = clientName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.technologyStack = technologyStack;
        this.budget = budget;
        this.projectStatus = projectStatus;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getTechnologyStack() {
        return technologyStack;
    }

    public void setTechnologyStack(String technologyStack) {
        this.technologyStack = technologyStack;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }
}
