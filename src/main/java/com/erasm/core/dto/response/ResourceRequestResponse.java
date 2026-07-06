package com.erasm.core.dto.response;

import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResourceRequestResponse {

    private Long requestId;
    private Long projectId;
    private String projectName;
    private List<RequestSkillResponse> skills = new ArrayList<>();
    private RequestStatus status;
    private String requestedBy;
    private LocalDate requestDate;
    private String remarks;

    public ResourceRequestResponse() {
    }

    public ResourceRequestResponse(Long requestId, Long projectId, String projectName, List<RequestSkillResponse> skills, RequestStatus status, String requestedBy, LocalDate requestDate, String remarks) {
        this.requestId = requestId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.skills = skills;
        this.status = status;
        this.requestedBy = requestedBy;
        this.requestDate = requestDate;
        this.remarks = remarks;
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

    public List<RequestSkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<RequestSkillResponse> skills) {
        this.skills = skills;
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

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getCreatedDate() {
        return requestDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.requestDate = createdDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Legacy support methods for compatibility
    public Long getSkillId() {
        return (skills != null && !skills.isEmpty()) ? skills.get(0).getSkillId() : null;
    }

    public void setSkillId(Long skillId) {
        ensureFirstSkillExist();
        skills.get(0).setSkillId(skillId);
    }

    public String getSkillName() {
        return (skills != null && !skills.isEmpty()) ? skills.get(0).getSkillName() : null;
    }

    public void setSkillName(String skillName) {
        ensureFirstSkillExist();
        skills.get(0).setSkillName(skillName);
    }

    public Integer getRequiredCount() {
        return (skills != null && !skills.isEmpty()) ? skills.get(0).getRequiredCount() : null;
    }

    public void setRequiredCount(Integer requiredCount) {
        ensureFirstSkillExist();
        skills.get(0).setRequiredCount(requiredCount);
    }

    public SkillLevel getRequiredLevel() {
        return (skills != null && !skills.isEmpty()) ? skills.get(0).getRequiredLevel() : null;
    }

    public void setRequiredLevel(SkillLevel requiredLevel) {
        ensureFirstSkillExist();
        skills.get(0).setRequiredLevel(requiredLevel);
    }

    private void ensureFirstSkillExist() {
        if (skills == null) {
            skills = new ArrayList<>();
        }
        if (skills.isEmpty()) {
            skills.add(new RequestSkillResponse());
        }
    }
}
