package com.erasm.core.dto.request;

import com.erasm.core.enums.SkillLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RequestSkillDto {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Required count is mandatory")
    @Min(value = 1, message = "Required count must be at least 1")
    private Integer requiredCount;

    @NotNull(message = "Required level is mandatory")
    private SkillLevel requiredLevel;

    public RequestSkillDto() {
    }

    public RequestSkillDto(Long skillId, Integer requiredCount, SkillLevel requiredLevel) {
        this.skillId = skillId;
        this.requiredCount = requiredCount;
        this.requiredLevel = requiredLevel;
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
}
