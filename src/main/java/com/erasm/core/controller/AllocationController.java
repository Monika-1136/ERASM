package com.erasm.core.controller;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.service.AllocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/allocations", "/api/allocations"})
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ApiResponse<AllocationResponse>> allocateEmployee(@Valid @RequestBody AllocationRequest request) {
        AllocationResponse response = allocationService.allocateEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee allocated successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ApiResponse<AllocationResponse>> reallocateEmployee(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body) {
        Object val = body.get("percentage");
        if (val == null) {
            val = body.get("allocationPercentage");
        }
        if (val == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Request body must contain 'percentage' field."));
        }
        Double percentage;
        try {
            percentage = Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid value for 'percentage': must be a numeric value between 1 and 100."));
        }
        AllocationResponse response = allocationService.reallocateEmployee(id, percentage);
        return ResponseEntity.ok(ApiResponse.success("Employee reallocated successfully", response));
    }

    @PutMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ApiResponse<AllocationResponse>> releaseEmployee(@PathVariable Long id) {
        AllocationResponse response = allocationService.releaseEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee released successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ApiResponse<AllocationResponse>> updateAllocationStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Request body must contain 'status' field."));
        }
        AllocationStatus status;
        try {
            status = AllocationStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String validValues = java.util.Arrays.stream(AllocationStatus.values())
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status '" + statusStr + "'. Accepted values are: [" + validValues + "]"));
        }
        AllocationResponse response = allocationService.updateAllocationStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Allocation status updated successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<AllocationResponse>> getAllocationById(@PathVariable Long id) {
        AllocationResponse response = allocationService.getAllocationById(id);
        return ResponseEntity.ok(ApiResponse.success("Allocation fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllAllocations() {
        List<AllocationResponse> response = allocationService.getAllAllocations();
        return ResponseEntity.ok(ApiResponse.success("All allocations fetched successfully", response));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'EMPLOYEE', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllocationsByEmployee(@PathVariable Long employeeId) {
        List<AllocationResponse> response = allocationService.getAllocationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Employee allocations fetched successfully", response));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllocationsByProject(@PathVariable Long projectId) {
        List<AllocationResponse> response = allocationService.getAllocationsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project allocations fetched successfully", response));
    }
}
