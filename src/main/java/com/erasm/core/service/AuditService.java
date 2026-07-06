package com.erasm.core.service;

import com.erasm.core.dto.response.AuditLogResponse;
import java.util.List;

public interface AuditService {
    void logAction(String action, String entityName, Long entityId, String performedBy, String details);
    void logAction(Long userId, String action, String entityName, Long entityId, String oldValue, String newValue, String ipAddress);
    List<AuditLogResponse> getAllAuditLogs();
}
