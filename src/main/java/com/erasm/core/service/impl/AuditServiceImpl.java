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
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(log -> new AuditLogResponse(
                        log.getLogId(),
                        log.getAction(),
                        log.getEntityName(),
                        log.getEntityId(),
                        log.getPerformedBy(),
                        log.getTimestamp(),
                        log.getDetails()
                )).collect(Collectors.toList());
    }
}
