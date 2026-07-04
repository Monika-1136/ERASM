package com.erasm.core.dto.response;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long logId;
    private String action;
    private String entityName;
    private Long entityId;
    private String performedBy;
    private LocalDateTime timestamp;
    private String details;

    public AuditLogResponse() {
    }

    public AuditLogResponse(Long logId, String action, String entityName, Long entityId, String performedBy, LocalDateTime timestamp, String details) {
        this.logId = logId;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.details = details;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
