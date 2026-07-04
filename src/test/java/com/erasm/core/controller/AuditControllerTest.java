package com.erasm.core.controller;

import com.erasm.core.dto.response.AuditLogResponse;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    private AuditLogResponse auditLogResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();

        auditLogResponse = new AuditLogResponse();
        auditLogResponse.setLogId(1L);
        auditLogResponse.setAction("CREATE_EMPLOYEE");
        auditLogResponse.setEntityName("Employee");
        auditLogResponse.setEntityId(1L);
        auditLogResponse.setPerformedBy("admin@erasm.com");
        auditLogResponse.setTimestamp(LocalDateTime.now());
        auditLogResponse.setDetails("Created employee profile");
    }

    @Test
    void testGetAllAuditLogs_Success() throws Exception {
        when(auditService.getAllAuditLogs()).thenReturn(Collections.singletonList(auditLogResponse));

        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].logId").value(1L))
                .andExpect(jsonPath("$.data[0].action").value("CREATE_EMPLOYEE"));

        verify(auditService).getAllAuditLogs();
    }
}
