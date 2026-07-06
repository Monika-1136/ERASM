package com.erasm.core.controller;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.request.ReallocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.service.AllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@Tag(name = "Allocation Management", description = "Endpoints for managing employee allocations to projects")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Allocate employee to project",
        description = "Creates a new allocation for an employee on a project, optionally linked to a resource request.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Employee allocated successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request details or validation failure")
        }
    )
    public ResponseEntity<ApiResponse<AllocationResponse>> allocateEmployee(@Valid @RequestBody AllocationRequest request) {
        AllocationResponse response = allocationService.allocateEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee allocated successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Reallocate employee",
        description = "Updates the allocation percentage for an existing allocation.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee reallocated successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid percentage or allocation ID")
        }
    )
    public ResponseEntity<ApiResponse<AllocationResponse>> reallocateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody ReallocationRequest request) {
        AllocationResponse response = allocationService.reallocateEmployee(id, request.getAllocationPercentage());
        return ResponseEntity.ok(ApiResponse.success("Employee reallocated successfully", response));
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Release employee from project",
        description = "Releases an employee from a project by setting the allocation status to RELEASED.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee released successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid allocation ID")
        }
    )
    public ResponseEntity<ApiResponse<AllocationResponse>> releaseEmployee(@PathVariable Long id) {
        AllocationResponse response = allocationService.releaseEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee released successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Delete allocation",
        description = "Deletes an allocation record completely from the database.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Allocation deleted successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid allocation ID")
        }
    )
    public ResponseEntity<ApiResponse<Void>> deleteAllocation(@PathVariable Long id) {
        allocationService.deleteAllocation(id);
        return ResponseEntity.ok(ApiResponse.success("Allocation deleted successfully"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Update allocation status",
        description = "Updates the status of an allocation using a string request body representing the status.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Allocation status updated successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value or allocation ID")
        }
    )
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
            status = AllocationStatus.fromString(statusStr);
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
    @Operation(
        summary = "Get allocation by ID",
        description = "Retrieves details of an allocation record by its ID.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Allocation fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid allocation ID")
        }
    )
    public ResponseEntity<ApiResponse<AllocationResponse>> getAllocationById(@PathVariable Long id) {
        AllocationResponse response = allocationService.getAllocationById(id);
        return ResponseEntity.ok(ApiResponse.success("Allocation fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    @Operation(
        summary = "Get all allocations",
        description = "Retrieves all allocation records.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All allocations fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllAllocations() {
        List<AllocationResponse> response = allocationService.getAllAllocations();
        return ResponseEntity.ok(ApiResponse.success("All allocations fetched successfully", response));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'EMPLOYEE', 'AUDITOR')")
    @Operation(
        summary = "Get employee allocations",
        description = "Retrieves all allocation records for a specific employee.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee allocations fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllocationsByEmployee(@PathVariable Long employeeId) {
        List<AllocationResponse> response = allocationService.getAllocationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Employee allocations fetched successfully", response));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    @Operation(
        summary = "Get project allocations",
        description = "Retrieves all allocation records for a specific project.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project allocations fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getAllocationsByProject(@PathVariable Long projectId) {
        List<AllocationResponse> response = allocationService.getAllocationsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project allocations fetched successfully", response));
    }
}
