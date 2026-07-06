package com.erasm.core.dto.response;

import com.erasm.core.enums.SkillLevel;

public class RequestSkillResponse {

    private Long requestSkillId;
    private Long skillId;
    private String skillName;
    private Integer requiredCount;
    private SkillLevel requiredLevel;

    public RequestSkillResponse() {
    }

    public RequestSkillResponse(Long requestSkillId, Long skillId, String skillName, Integer requiredCount, SkillLevel requiredLevel) {
        this.requestSkillId = requestSkillId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.requiredCount = requiredCount;
        this.requiredLevel = requiredLevel;
    }

    public Long getRequestSkillId() {
        return requestSkillId;
    }

    public void setRequestSkillId(Long requestSkillId) {
        this.requestSkillId = requestSkillId;
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
}
