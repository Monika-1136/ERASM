package com.erasm.core.entity;

import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "resource_requests")
public class ResourceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"resourceRequests", "allocations"})
    private Project project;

    @OneToMany(mappedBy = "resourceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<RequestSkill> requestSkills = new java.util.ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column
    private String requestedBy;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column
    private String remarks;

    public ResourceRequest() {
    }

    public ResourceRequest(Long requestId, Project project, RequestStatus status, String requestedBy, LocalDate requestDate, String remarks) {
        this.requestId = requestId;
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public java.util.List<RequestSkill> getRequestSkills() {
        return requestSkills;
    }

    public void setRequestSkills(java.util.List<RequestSkill> requestSkills) {
        this.requestSkills = requestSkills;
        if (requestSkills != null) {
            for (RequestSkill rs : requestSkills) {
                rs.setResourceRequest(this);
            }
        }
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

    // Backward compatibility helper methods
    public Skill getSkill() {
        return (requestSkills != null && !requestSkills.isEmpty()) ? requestSkills.get(0).getSkill() : null;
    }

    public void setSkill(Skill skill) {
        ensureFirstSkillExist();
        requestSkills.get(0).setSkill(skill);
    }

    public Integer getRequiredCount() {
        return (requestSkills != null && !requestSkills.isEmpty()) ? requestSkills.get(0).getRequiredCount() : null;
    }

    public void setRequiredCount(Integer requiredCount) {
        ensureFirstSkillExist();
        requestSkills.get(0).setRequiredCount(requiredCount);
    }

    public SkillLevel getRequiredLevel() {
        return (requestSkills != null && !requestSkills.isEmpty()) ? requestSkills.get(0).getRequiredLevel() : null;
    }

    public void setRequiredLevel(SkillLevel requiredLevel) {
        ensureFirstSkillExist();
        requestSkills.get(0).setRequiredLevel(requiredLevel);
    }

    private void ensureFirstSkillExist() {
        if (requestSkills == null) {
            requestSkills = new java.util.ArrayList<>();
        }
        if (requestSkills.isEmpty()) {
            RequestSkill rs = new RequestSkill();
            rs.setResourceRequest(this);
            requestSkills.add(rs);
        }
    }
}
