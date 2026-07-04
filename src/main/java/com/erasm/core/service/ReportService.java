package com.erasm.core.service;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.dto.response.DashboardResponse;
import java.util.List;

public interface ReportService {
    List<SkillReportResponse> getSkillReport();
    List<UtilizationReportResponse> getUtilizationReport();
    List<AllocationResponse> getProjectAllocationReport();
    DashboardResponse getDashboardMetrics();
}
