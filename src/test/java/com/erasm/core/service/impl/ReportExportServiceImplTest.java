package com.erasm.core.service.impl;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.entity.AuditLog;
import com.erasm.core.exception.ReportGenerationException;
import com.erasm.core.repository.AuditLogRepository;
import com.erasm.core.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportExportServiceImplTest {

    @Mock
    private ReportService reportService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ReportExportServiceImpl reportExportService;

    private AllocationResponse allocationResponse;
    private UtilizationReportResponse utilizationReportResponse;
    private SkillReportResponse skillReportResponse;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        allocationResponse = new AllocationResponse();
        allocationResponse.setAllocationId(100L);
        allocationResponse.setProjectName("Project X");
        allocationResponse.setEmployeeName("Alice Smith");
        allocationResponse.setAllocationPercentage(50.0);
        allocationResponse.setStartDate(LocalDate.now());
        allocationResponse.setStatus(com.erasm.core.enums.AllocationStatus.ACTIVE);

        utilizationReportResponse = new UtilizationReportResponse(1L, "Alice Smith", "Engineering", 50.0, 50.0, 50.0);

        skillReportResponse = new SkillReportResponse(10L, "Java", "Backend", 1, Collections.emptyList());

        auditLog = new AuditLog();
        auditLog.setLogId(200L);
        auditLog.setAction("CREATE");
        auditLog.setEntityName("Employee");
        auditLog.setEntityId(1L);
        auditLog.setPerformedBy("admin@erasm.com");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails("Created Alice Smith");
    }

    @Test
    void testExportProjectsPdf_Success() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));
        byte[] pdfBytes = reportExportService.exportProjectsPdf();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testExportProjectsPdf_Error() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportProjectsPdf());
    }

    @Test
    void testExportUtilizationPdf_Success() {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(utilizationReportResponse));
        byte[] pdfBytes = reportExportService.exportUtilizationPdf();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testExportUtilizationPdf_Error() {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportUtilizationPdf());
    }

    @Test
    void testExportSkillsPdf_Success() {
        when(reportService.getSkillReport()).thenReturn(Collections.singletonList(skillReportResponse));
        byte[] pdfBytes = reportExportService.exportSkillsPdf();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testExportSkillsPdf_Error() {
        when(reportService.getSkillReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportSkillsPdf());
    }

    @Test
    void testExportAuditPdf_Success() {
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(auditLog));
        byte[] pdfBytes = reportExportService.exportAuditPdf();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testExportAuditPdf_Error() {
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportAuditPdf());
    }

    @Test
    void testExportProjectsExcel_Success() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));
        byte[] excelBytes = reportExportService.exportProjectsExcel();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
    }

    @Test
    void testExportProjectsExcel_Error() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportProjectsExcel());
    }

    @Test
    void testExportUtilizationExcel_Success() {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(utilizationReportResponse));
        byte[] excelBytes = reportExportService.exportUtilizationExcel();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
    }

    @Test
    void testExportUtilizationExcel_Error() {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportUtilizationExcel());
    }

    @Test
    void testExportSkillsExcel_Success() {
        when(reportService.getSkillReport()).thenReturn(Collections.singletonList(skillReportResponse));
        byte[] excelBytes = reportExportService.exportSkillsExcel();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
    }

    @Test
    void testExportSkillsExcel_Error() {
        when(reportService.getSkillReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportSkillsExcel());
    }

    @Test
    void testExportAuditExcel_Success() {
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(auditLog));
        byte[] excelBytes = reportExportService.exportAuditExcel();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
    }

    @Test
    void testExportAuditExcel_Error() {
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportAuditExcel());
    }

    @Test
    void testExportProjectsWord_Success() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));
        byte[] wordBytes = reportExportService.exportProjectsWord();
        assertNotNull(wordBytes);
        assertTrue(wordBytes.length > 0);
    }

    @Test
    void testExportProjectsWord_Error() {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(null));
        assertThrows(ReportGenerationException.class, () -> reportExportService.exportProjectsWord());
    }
}
