package com.erasm.core.service;

public interface ReportExportService {
    byte[] exportProjectsPdf();
    byte[] exportProjectsExcel();
    byte[] exportProjectsWord();

    byte[] exportUtilizationPdf();
    byte[] exportUtilizationExcel();

    byte[] exportSkillsPdf();
    byte[] exportSkillsExcel();

    byte[] exportAuditPdf();
    byte[] exportAuditExcel();
}
