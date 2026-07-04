package com.erasm.core.controller;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.dto.response.DashboardResponse;
import com.erasm.core.service.ReportService;
import com.erasm.core.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/reports", "/api/reports"})
@PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(ReportService reportService, ReportExportService reportExportService) {
        this.reportService = reportService;
        this.reportExportService = reportExportService;
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

    // ==========================================
    // PDF Exports
    // ==========================================
    @GetMapping("/projects/pdf")
    public ResponseEntity<byte[]> downloadProjectsPdf() {
        byte[] content = reportExportService.exportProjectsPdf();
        return createAttachmentResponse(content, "projects_allocation_report.pdf", "application/pdf");
    }

    @GetMapping("/utilization/pdf")
    public ResponseEntity<byte[]> downloadUtilizationPdf() {
        byte[] content = reportExportService.exportUtilizationPdf();
        return createAttachmentResponse(content, "utilization_report.pdf", "application/pdf");
    }

    @GetMapping("/skills/pdf")
    public ResponseEntity<byte[]> downloadSkillsPdf() {
        byte[] content = reportExportService.exportSkillsPdf();
        return createAttachmentResponse(content, "skills_report.pdf", "application/pdf");
    }

    @GetMapping("/audit/pdf")
    public ResponseEntity<byte[]> downloadAuditPdf() {
        byte[] content = reportExportService.exportAuditPdf();
        return createAttachmentResponse(content, "audit_log_report.pdf", "application/pdf");
    }

    // ==========================================
    // Excel Exports
    // ==========================================
    @GetMapping("/projects/excel")
    public ResponseEntity<byte[]> downloadProjectsExcel() {
        byte[] content = reportExportService.exportProjectsExcel();
        return createAttachmentResponse(content, "projects_allocation_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/utilization/excel")
    public ResponseEntity<byte[]> downloadUtilizationExcel() {
        byte[] content = reportExportService.exportUtilizationExcel();
        return createAttachmentResponse(content, "utilization_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/skills/excel")
    public ResponseEntity<byte[]> downloadSkillsExcel() {
        byte[] content = reportExportService.exportSkillsExcel();
        return createAttachmentResponse(content, "skills_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/audit/excel")
    public ResponseEntity<byte[]> downloadAuditExcel() {
        byte[] content = reportExportService.exportAuditExcel();
        return createAttachmentResponse(content, "audit_log_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    // ==========================================
    // Word Export
    // ==========================================
    @GetMapping("/projects/word")
    public ResponseEntity<byte[]> downloadProjectsWord() {
        byte[] content = reportExportService.exportProjectsWord();
        return createAttachmentResponse(content, "projects_allocation_report.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private ResponseEntity<byte[]> createAttachmentResponse(byte[] content, String filename, String mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(mediaType))
                .body(content);
    }
}
