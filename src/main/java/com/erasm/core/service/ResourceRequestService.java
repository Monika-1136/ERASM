package com.erasm.core.service;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.enums.RequestStatus;
import java.util.List;

public interface ResourceRequestService {
    ResourceRequestResponse createResourceRequest(ResourceRequestDto request);
    ResourceRequestResponse getRequestById(Long id);
    List<ResourceRequestResponse> getAllRequests();
    List<ResourceRequestResponse> getRequestsByProject(Long projectId);
    ResourceRequestResponse updateRequestStatus(Long id, RequestStatus status);
}
