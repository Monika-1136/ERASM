package com.erasm.core.controller;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.enums.RequestStatus;
import com.erasm.core.service.ResourceRequestService;
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
@RequestMapping("/api/resource-requests")
@Tag(name = "Resource Request Management", description = "Endpoints for managing project resource requests and skill requirements")
public class ResourceRequestController {

    private final ResourceRequestService resourceRequestService;

    public ResourceRequestController(ResourceRequestService resourceRequestService) {
        this.resourceRequestService = resourceRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER')")
    @Operation(
        summary = "Create resource request",
        description = "Creates a new resource request with the specified project and skill requirements.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Resource request created successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request details or validation failure")
        }
    )
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> createResourceRequest(@Valid @RequestBody ResourceRequestDto request) {
        ResourceRequestResponse response = resourceRequestService.createResourceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource request created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    @Operation(
        summary = "Get resource request by ID",
        description = "Retrieves a resource request by its ID.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resource request fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Resource request not found")
        }
    )
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> getRequestById(@PathVariable Long id) {
        ResourceRequestResponse response = resourceRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Resource request fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    @Operation(
        summary = "Get all resource requests",
        description = "Retrieves all resource requests.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All resource requests fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<List<ResourceRequestResponse>>> getAllRequests() {
        List<ResourceRequestResponse> response = resourceRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All resource requests fetched successfully", response));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    @Operation(
        summary = "Get requests by project ID",
        description = "Retrieves all resource requests for a specific project.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resource requests for project fetched successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<List<ResourceRequestResponse>>> getRequestsByProject(@PathVariable Long projectId) {
        List<ResourceRequestResponse> response = resourceRequestService.getRequestsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Resource requests for project fetched successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(
        summary = "Update resource request status",
        description = "Updates the workflow status of a resource request (e.g. SUBMITTED -> APPROVED).",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resource request status updated successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value or workflow transition failure")
        }
    )
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Request body must contain 'status' field."));
        }
        RequestStatus status;
        try {
            status = RequestStatus.fromString(statusStr);
        } catch (IllegalArgumentException e) {
            String validValues = java.util.Arrays.stream(RequestStatus.values())
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status '" + statusStr + "'. Accepted values are: [" + validValues + "]"));
        }
        ResourceRequestResponse response = resourceRequestService.updateRequestStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Resource request status updated successfully", response));
    }
}
