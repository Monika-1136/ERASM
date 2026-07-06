package com.erasm.core.controller;

import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for resource availability queries.
 * Provides endpoints to identify employees available for allocation
 * based on current utilization and skill requirements.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final EmployeeService employeeService;

    public ResourceController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Returns a list of employees who are available for allocation.
     * An employee is considered available if their total allocation percentage
     * is below the specified threshold (default: 100%).
     *
     * @param skillId       optional — filter by required skill ID
     * @param maxAllocation optional — maximum current allocation % (default 100)
     * @return list of available employees with their current allocation details
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAvailableEmployees(
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minExperience,
            @RequestParam(required = false, defaultValue = "100") Double maxAllocation) {
        List<EmployeeResponse> available = employeeService.getAvailableEmployees(skillId, department, minExperience, maxAllocation);
        return ResponseEntity.ok(ApiResponse.success(
                "Available employees fetched successfully. Total: " + available.size(), available));
    }
}
