package com.erasm.core.dto.request;

import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ResourceRequestDto {

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotEmpty(message = "At least one skill requirement is required")
    private List<@Valid RequestSkillDto> skills = new ArrayList<>();

    private RequestStatus status;

    private String remarks;

    public ResourceRequestDto() {
    }

    public ResourceRequestDto(Long projectId, List<RequestSkillDto> skills, RequestStatus status, String remarks) {
        this.projectId = projectId;
        this.skills = skills;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<RequestSkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<RequestSkillDto> skills) {
        this.skills = skills;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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
            skills.add(new RequestSkillDto());
        }
    }
}
