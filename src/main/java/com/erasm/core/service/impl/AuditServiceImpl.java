package com.erasm.core.service.impl;

import com.erasm.core.dto.response.AuditLogResponse;
import com.erasm.core.entity.AuditLog;
import com.erasm.core.repository.AuditLogRepository;
import com.erasm.core.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void logAction(String action, String entityName, Long entityId, String performedBy, String details) {
        logger.info("Audit log action: {} on {} (ID: {}) by {}", action, entityName, entityId, performedBy);
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setPerformedBy(performedBy != null ? performedBy : "SYSTEM");
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional
    public void logAction(Long userId, String action, String entityName, Long entityId, String oldValue, String newValue, String ipAddress) {
        logger.info("Audit log action: {} on {} (ID: {}) by user ID: {}", action, entityName, entityId, userId);
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setIpAddress(ipAddress != null ? ipAddress : "127.0.0.1");
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setPerformedBy("User ID: " + userId);
        auditLog.setDetails("Action: " + action + " on " + entityName + " (ID: " + entityId + ")");
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(log -> new AuditLogResponse(
                        log.getLogId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityName(),
                        log.getEntityId(),
                        log.getOldValue(),
                        log.getNewValue(),
                        log.getIpAddress(),
                        log.getCreatedAt(),
                        log.getPerformedBy(),
                        log.getDetails()
                )).collect(Collectors.toList());
    }
}
