package com.erasm.core.controller;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.dto.response.DashboardResponse;
import com.erasm.core.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<SkillReportResponse>>> getSkillReport() {
        List<SkillReportResponse> response = reportService.getSkillReport();
        return ResponseEntity.ok(ApiResponse.success("Skill report generated successfully", response));
    }

    @GetMapping("/utilization")
    public ResponseEntity<ApiResponse<List<UtilizationReportResponse>>> getUtilizationReport() {
        List<UtilizationReportResponse> response = reportService.getUtilizationReport();
        return ResponseEntity.ok(ApiResponse.success("Utilization report generated successfully", response));
    }

    @GetMapping("/allocations")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getProjectAllocationReport() {
        List<AllocationResponse> response = reportService.getProjectAllocationReport();
        return ResponseEntity.ok(ApiResponse.success("Project allocation report generated successfully", response));
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getProjectReport() {
        List<AllocationResponse> response = reportService.getProjectAllocationReport();
        return ResponseEntity.ok(ApiResponse.success("Project allocation report generated successfully", response));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardMetrics() {
        DashboardResponse response = reportService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics generated successfully", response));
    }
}
