package com.erasm.core.controller;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.DashboardResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.service.ReportService;
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

        mockMvc.perform(get("/api/reports/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].skillId").value(1L))
                .andExpect(jsonPath("$.data[0].skillName").value("Java"));

        verify(reportService).getSkillReport();
    }

    @Test
    void testGetUtilizationReport_Success() throws Exception {
        when(reportService.getUtilizationReport()).thenReturn(Collections.singletonList(utilizationReportResponse));

        mockMvc.perform(get("/api/reports/utilization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].employeeId").value(1L))
                .andExpect(jsonPath("$.data[0].employeeName").value("John Doe"));

        verify(reportService).getUtilizationReport();
    }

    @Test
    void testGetProjectAllocationReport_Success() throws Exception {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));

        mockMvc.perform(get("/api/reports/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));

        verify(reportService).getProjectAllocationReport();
    }

    @Test
    void testGetProjectReport_Success() throws Exception {
        when(reportService.getProjectAllocationReport()).thenReturn(Collections.singletonList(allocationResponse));

        mockMvc.perform(get("/api/reports/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));
    }

    @Test
    void testGetDashboardMetrics_Success() throws Exception {
        DashboardResponse mockResponse = new DashboardResponse(75.0, 25.0, 75.0, 25.0, 75.0, 5L, 2L, 0L);
        when(reportService.getDashboardMetrics()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectCount").value(5))
                .andExpect(jsonPath("$.data.benchEmployeesCount").value(2));

        verify(reportService).getDashboardMetrics();
    }
}
