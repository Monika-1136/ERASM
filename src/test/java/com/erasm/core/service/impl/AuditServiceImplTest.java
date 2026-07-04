package com.erasm.core.service.impl;

import com.erasm.core.dto.response.AuditLogResponse;
import com.erasm.core.entity.AuditLog;
import com.erasm.core.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    void testLogAction_Success() {
        auditService.logAction("CREATE", "Project", 1L, "admin@erasm.com", "Created project");

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void testGetAllAuditLogs_Success() {
        AuditLog auditLog = new AuditLog();
        auditLog.setLogId(10L);
        auditLog.setAction("CREATE");
        auditLog.setEntityName("Project");
        auditLog.setEntityId(1L);
        auditLog.setPerformedBy("admin@erasm.com");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails("Created project");

        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(auditLog));

        List<AuditLogResponse> logs = auditService.getAllAuditLogs();

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("CREATE", logs.get(0).getAction());
        assertEquals("Project", logs.get(0).getEntityName());
    }
}
