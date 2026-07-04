package com.erasm.core.service;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.enums.AllocationStatus;
import java.util.List;

public interface AllocationService {
    AllocationResponse allocateEmployee(AllocationRequest request);
    AllocationResponse reallocateEmployee(Long allocationId, Double newPercentage);
    AllocationResponse releaseEmployee(Long allocationId);
    AllocationResponse updateAllocationStatus(Long allocationId, AllocationStatus status);
    AllocationResponse getAllocationById(Long id);
    List<AllocationResponse> getAllAllocations();
    List<AllocationResponse> getAllocationsByEmployee(Long employeeId);
    List<AllocationResponse> getAllocationsByProject(Long projectId);
}
