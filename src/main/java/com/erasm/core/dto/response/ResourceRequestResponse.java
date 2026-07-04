package com.erasm.core.dto.response;

import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import java.time.LocalDate;

public class ResourceRequestResponse {

    private Long requestId;
    private Long projectId;
    private String projectName;
    private Long skillId;
    private String skillName;
    private Integer requiredCount;
    private SkillLevel requiredLevel;
    private RequestStatus status;
    private String requestedBy;
    private LocalDate createdDate;

    public ResourceRequestResponse() {
    }

    public ResourceRequestResponse(Long requestId, Long projectId, String projectName, Long skillId, String skillName, Integer requiredCount, SkillLevel requiredLevel, RequestStatus status, String requestedBy, LocalDate createdDate) {
        this.requestId = requestId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.skillId = skillId;
        this.skillName = skillName;
        this.requiredCount = requiredCount;
        this.requiredLevel = requiredLevel;
        this.status = status;
        this.requestedBy = requestedBy;
        this.createdDate = createdDate;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public SkillLevel getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(SkillLevel requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}
