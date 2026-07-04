package com.erasm.core.controller;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.DashboardResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.exception.ReportGenerationException;
import com.erasm.core.service.ReportService;
import com.erasm.core.service.ReportExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @Mock
    private ReportExportService reportExportService;

    @InjectMocks
    private ReportController reportController;

    private SkillReportResponse skillReportResponse;
    private UtilizationReportResponse utilizationReportResponse;
    private AllocationResponse allocationResponse;

    @BeforeEach
    void setUp() {
        // Integrate ExceptionHandler in standalone setup to test exception responses
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new com.erasm.core.exception.GlobalExceptionHandler())
                .build();

        skillReportResponse = new SkillReportResponse();
        skillReportResponse.setSkillId(1L);
        skillReportResponse.setSkillName("Java");
        skillReportResponse.setCategory("Backend");
        skillReportResponse.setCount(1);
        skillReportResponse.setEmployeeSkills(Collections.emptyList());

        utilizationReportResponse = new UtilizationReportResponse();
        utilizationReportResponse.setEmployeeId(1L);
        utilizationReportResponse.setEmployeeName("John Doe");
        utilizationReportResponse.setDepartment("Engineering");
        utilizationReportResponse.setTotalAllocatedPercentage(80.0);
        utilizationReportResponse.setBillablePercentage(80.0);
        utilizationReportResponse.setBenchPercentage(20.0);

        allocationResponse = new AllocationResponse();
        allocationResponse.setAllocationId(1L);
        allocationResponse.setEmployeeId(1L);
        allocationResponse.setEmployeeName("John Doe");
        allocationResponse.setProjectId(1L);
        allocationResponse.setProjectName("Healthcare Portal");
        allocationResponse.setAllocationPercentage(80.0);
        allocationResponse.setStartDate(LocalDate.now());
        allocationResponse.setStatus(AllocationStatus.ACTIVE);
    }

    @Test
    void testGetSkillReport_Success() throws Exception {
        when(reportService.getSkillReport()).thenReturn(Collections.singletonList(skillReportResponse));

        mockMvc.perform(get("/reports/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].skillId").value(1L))
                .andExpect(jsonPath("$.data[0].skillName").value("Java"));

        verify(reportService).getSkillReport();
    }

    @Test
    void testGetUtilizationReport_Success() throws Exception {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(utilizationReportResponse));

        mockMvc.perform(get("/reports/utilization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].employeeId").value(1L))
                .andExpect(jsonPath("$.data[0].employeeName").value("John Doe"));

        verify(reportService).getUtilizationReport();
    }

    @Test
    void testGetProjectAllocationReport_Success() throws Exception {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));

        mockMvc.perform(get("/reports/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));

        verify(reportService).getProjectAllocationReport();
    }

    @Test
    void testGetProjectReport_Success() throws Exception {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));

        mockMvc.perform(get("/reports/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));
    }

    @Test
    void testGetDashboardMetrics_Success() throws Exception {
        DashboardResponse mockResponse = new DashboardResponse(75.0, 25.0, 75.0, 25.0, 75.0, 5L, 2L, 0L);
        when(reportService.getDashboardMetrics()).thenReturn(mockResponse);

        mockMvc.perform(get("/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectCount").value(5))
                .andExpect(jsonPath("$.data.benchEmployeesCount").value(2));

        verify(reportService).getDashboardMetrics();
    }

    @Test
    void testDownloadProjectsPdf() throws Exception {
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        when(reportExportService.exportProjectsPdf()).thenReturn(pdfBytes);

        mockMvc.perform(get("/reports/projects/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"projects_allocation_report.pdf\""))
                .andExpect(content().bytes(pdfBytes));

        verify(reportExportService).exportProjectsPdf();
    }

    @Test
    void testDownloadProjectsExcel() throws Exception {
        byte[] excelBytes = "dummy-excel-content".getBytes();
        when(reportExportService.exportProjectsExcel()).thenReturn(excelBytes);

        mockMvc.perform(get("/reports/projects/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"projects_allocation_report.xlsx\""))
                .andExpect(content().bytes(excelBytes));

        verify(reportExportService).exportProjectsExcel();
    }

    @Test
    void testDownloadProjectsWord() throws Exception {
        byte[] wordBytes = "dummy-word-content".getBytes();
        when(reportExportService.exportProjectsWord()).thenReturn(wordBytes);

        mockMvc.perform(get("/reports/projects/word"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"projects_allocation_report.docx\""))
                .andExpect(content().bytes(wordBytes));

        verify(reportExportService).exportProjectsWord();
    }

    @Test
    void testDownloadUtilizationPdf() throws Exception {
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        when(reportExportService.exportUtilizationPdf()).thenReturn(pdfBytes);

        mockMvc.perform(get("/reports/utilization/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"utilization_report.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void testDownloadUtilizationExcel() throws Exception {
        byte[] excelBytes = "dummy-excel-content".getBytes();
        when(reportExportService.exportUtilizationExcel()).thenReturn(excelBytes);

        mockMvc.perform(get("/reports/utilization/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"utilization_report.xlsx\""))
                .andExpect(content().bytes(excelBytes));
    }

    @Test
    void testDownloadSkillsPdf() throws Exception {
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        when(reportExportService.exportSkillsPdf()).thenReturn(pdfBytes);

        mockMvc.perform(get("/reports/skills/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"skills_report.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void testDownloadSkillsExcel() throws Exception {
        byte[] excelBytes = "dummy-excel-content".getBytes();
        when(reportExportService.exportSkillsExcel()).thenReturn(excelBytes);

        mockMvc.perform(get("/reports/skills/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"skills_report.xlsx\""))
                .andExpect(content().bytes(excelBytes));
    }

    @Test
    void testDownloadAuditPdf() throws Exception {
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        when(reportExportService.exportAuditPdf()).thenReturn(pdfBytes);

        mockMvc.perform(get("/reports/audit/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"audit_log_report.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void testDownloadAuditExcel() throws Exception {
        byte[] excelBytes = "dummy-excel-content".getBytes();
        when(reportExportService.exportAuditExcel()).thenReturn(excelBytes);

        mockMvc.perform(get("/reports/audit/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"audit_log_report.xlsx\""))
                .andExpect(content().bytes(excelBytes));
    }

    @Test
    void testDownloadProjectsPdf_GenerationError() throws Exception {
        when(reportExportService.exportProjectsPdf()).thenThrow(new ReportGenerationException("Failed to generate PDF document"));

        mockMvc.perform(get("/reports/projects/pdf"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to generate PDF document"));
    }
}
