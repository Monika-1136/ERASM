package com.erasm.core.dto.request;

import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ResourceRequestDto {

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Required count is mandatory")
    @Min(value = 1, message = "Required count must be at least 1")
    private Integer requiredCount;

    @NotNull(message = "Required level is mandatory")
    private SkillLevel requiredLevel;

    private RequestStatus status;

    public ResourceRequestDto() {
    }

    public ResourceRequestDto(Long projectId, Long skillId, Integer requiredCount, SkillLevel requiredLevel, RequestStatus status) {
        this.projectId = projectId;
        this.skillId = skillId;
        this.requiredCount = requiredCount;
        this.requiredLevel = requiredLevel;
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
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
}
