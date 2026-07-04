package com.erasm.core.dto.request;

import com.erasm.core.enums.SkillLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class EmployeeSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Skill level is required")
    private SkillLevel skillLevel;

    @PositiveOrZero(message = "Experience years must be positive or zero")
    private Double experienceYears;

    public EmployeeSkillRequest() {
    }

    public EmployeeSkillRequest(Long skillId, SkillLevel skillLevel, Double experienceYears) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.experienceYears = experienceYears;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Double experienceYears) {
        this.experienceYears = experienceYears;
    }
}
