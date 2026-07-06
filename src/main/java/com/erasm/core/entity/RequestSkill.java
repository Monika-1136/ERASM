package com.erasm.core.entity;

import com.erasm.core.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "request_skills")
public class RequestSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_skill_id")
    private Long requestSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnoreProperties("requestSkills")
    private ResourceRequest resourceRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "required_count", nullable = false)
    private Integer requiredCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_level", nullable = false)
    private SkillLevel requiredLevel;

    public RequestSkill() {
    }

    public RequestSkill(Long requestSkillId, ResourceRequest resourceRequest, Skill skill, Integer requiredCount, SkillLevel requiredLevel) {
        this.requestSkillId = requestSkillId;
        this.resourceRequest = resourceRequest;
        this.skill = skill;
        this.requiredCount = requiredCount;
        this.requiredLevel = requiredLevel;
    }

    public Long getRequestSkillId() {
        return requestSkillId;
    }

    public void setRequestSkillId(Long requestSkillId) {
        this.requestSkillId = requestSkillId;
    }

    public ResourceRequest getResourceRequest() {
        return resourceRequest;
    }

    public void setResourceRequest(ResourceRequest resourceRequest) {
        this.resourceRequest = resourceRequest;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
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
