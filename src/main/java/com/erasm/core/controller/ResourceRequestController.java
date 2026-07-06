package com.erasm.core.controller;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.enums.RequestStatus;
import com.erasm.core.service.ResourceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-requests")
public class ResourceRequestController {

    private final ResourceRequestService resourceRequestService;

    public ResourceRequestController(ResourceRequestService resourceRequestService) {
        this.resourceRequestService = resourceRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER')")
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> createResourceRequest(@Valid @RequestBody ResourceRequestDto request) {
        ResourceRequestResponse response = resourceRequestService.createResourceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource request created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> getRequestById(@PathVariable Long id) {
        ResourceRequestResponse response = resourceRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Resource request fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<ResourceRequestResponse>>> getAllRequests() {
        List<ResourceRequestResponse> response = resourceRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All resource requests fetched successfully", response));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_MANAGER', 'RESOURCE_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<ResourceRequestResponse>>> getRequestsByProject(@PathVariable Long projectId) {
        List<ResourceRequestResponse> response = resourceRequestService.getRequestsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Resource requests for project fetched successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ApiResponse<ResourceRequestResponse>> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Request body must contain 'status' field."));
        }
        RequestStatus status;
        try {
            status = RequestStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String validValues = java.util.Arrays.stream(RequestStatus.values())
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.joining(", "));
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status '" + statusStr + "'. Accepted values are: [" + validValues + "]"));
        }
        ResourceRequestResponse response = resourceRequestService.updateRequestStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Resource request status updated successfully", response));
    }
}
