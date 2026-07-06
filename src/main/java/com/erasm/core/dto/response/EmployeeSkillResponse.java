package com.erasm.core.dto.response;

import com.erasm.core.enums.SkillLevel;

public class EmployeeSkillResponse {

    private Long employeeSkillId;
    private Long skillId;
    private String skillName;
    private SkillLevel skillLevel;
    private Double experienceYears;

    public EmployeeSkillResponse() {
    }

    public EmployeeSkillResponse(Long employeeSkillId, Long skillId, String skillName, SkillLevel skillLevel, Double experienceYears) {
        this.employeeSkillId = employeeSkillId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.skillLevel = skillLevel;
        this.experienceYears = experienceYears;
    }

    public Long getEmployeeSkillId() {
        return employeeSkillId;
    }

    public void setEmployeeSkillId(Long employeeSkillId) {
        this.employeeSkillId = employeeSkillId;
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
