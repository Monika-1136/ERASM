package com.erasm.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private String category;
    private String description;

    public SkillRequest() {
    }

    public SkillRequest(String skillName, String category, String description) {
        this.skillName = skillName;
        this.category = category;
        this.description = description;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
