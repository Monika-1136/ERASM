package com.erasm.core.service.impl;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.entity.Allocation;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.Project;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.exception.AllocationException;
import com.erasm.core.exception.ProjectNotFoundException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.mapper.AllocationMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.service.AllocationService;
import com.erasm.core.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AllocationServiceImpl implements AllocationService {

    private static final Logger logger = LoggerFactory.getLogger(AllocationServiceImpl.class);

    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final AllocationMapper allocationMapper;
    private final AuditService auditService;

    public AllocationServiceImpl(AllocationRepository allocationRepository,
                                  EmployeeRepository employeeRepository,
                                  ProjectRepository projectRepository,
                                  AllocationMapper allocationMapper,
                                  AuditService auditService) {
        this.allocationRepository = allocationRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.allocationMapper = allocationMapper;
        this.auditService = auditService;
    }

    private void validateAllocationCap(Long employeeId, Double newAllocationPercentage, Long currentAllocationId) {
        Double currentTotal = allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(
                employeeId, Arrays.asList(AllocationStatus.ACTIVE));
        if (currentTotal == null) {
            currentTotal = 0.0;
        }

        if (currentAllocationId != null) {
            Allocation existing = allocationRepository.findById(currentAllocationId).orElse(null);
            if (existing != null && existing.getStatus() == AllocationStatus.ACTIVE) {
                currentTotal -= existing.getAllocationPercentage();
            }
        }

        if (currentTotal + newAllocationPercentage > 100.0) {
            throw new AllocationException(String.format("Allocation failed. Total allocation would be %.1f%%, exceeding maximum allowed 100%% limit for employee ID %d", (currentTotal + newAllocationPercentage), employeeId));
        }
    }

    @Override
    @Transactional
    public AllocationResponse allocateEmployee(AllocationRequest request) {
        logger.info("Allocating employee ID {} to project ID {} at {}%", request.getEmployeeId(), request.getProjectId(), request.getAllocationPercentage());
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + request.getProjectId()));

        validateAllocationCap(request.getEmployeeId(), request.getAllocationPercentage(), null);

        Allocation allocation = new Allocation();
        allocation.setEmployee(employee);
        allocation.setProject(project);
        allocation.setAllocationPercentage(request.getAllocationPercentage());
        allocation.setStartDate(request.getStartDate());
        allocation.setEndDate(request.getEndDate());
        allocation.setStatus(request.getStatus() != null ? request.getStatus() : AllocationStatus.ACTIVE);

        Allocation saved = allocationRepository.save(allocation);
        auditService.logAction("ALLOCATE_EMPLOYEE", "Allocation", saved.getAllocationId(), "RESOURCE_MANAGER",
                String.format("Allocated employee %d to project %d at %.0f%%", request.getEmployeeId(), request.getProjectId(), request.getAllocationPercentage()));

        return allocationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AllocationResponse reallocateEmployee(Long allocationId, Double newPercentage) {
        logger.info("Reallocating allocation ID {} to new percentage {}%", allocationId, newPercentage);
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationException("Allocation record not found with ID: " + allocationId));

        validateAllocationCap(allocation.getEmployee().getEmployeeId(), newPercentage, allocationId);

        allocation.setAllocationPercentage(newPercentage);
        Allocation saved = allocationRepository.save(allocation);
        auditService.logAction("REALLOCATE_EMPLOYEE", "Allocation", allocationId, "RESOURCE_MANAGER", "Reallocated to " + newPercentage + "%");
        return allocationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AllocationResponse releaseEmployee(Long allocationId) {
        logger.info("Releasing allocation ID {}", allocationId);
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationException("Allocation record not found with ID: " + allocationId));

        allocation.setStatus(AllocationStatus.RELEASED);
        Allocation saved = allocationRepository.save(allocation);
        auditService.logAction("RELEASE_EMPLOYEE", "Allocation", allocationId, "RESOURCE_MANAGER", "Released allocation");
        return allocationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AllocationResponse updateAllocationStatus(Long allocationId, AllocationStatus status) {
        logger.info("Updating allocation ID {} status to {}", allocationId, status);
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationException("Allocation record not found with ID: " + allocationId));

        allocation.setStatus(status);
        Allocation saved = allocationRepository.save(allocation);
        auditService.logAction("UPDATE_ALLOCATION_STATUS", "Allocation", allocationId, "RESOURCE_MANAGER", "Status updated to " + status);
        return allocationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AllocationResponse getAllocationById(Long id) {
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new AllocationException("Allocation record not found with ID: " + id));
        return allocationMapper.toResponse(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll().stream()
                .map(allocationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllocationsByEmployee(Long employeeId) {
        return allocationRepository.findByEmployeeEmployeeId(employeeId).stream()
                .map(allocationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllocationsByProject(Long projectId) {
        return allocationRepository.findByProjectProjectId(projectId).stream()
                .map(allocationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllocation(Long allocationId) {
        logger.info("Deleting allocation ID {}", allocationId);
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationException("Allocation record not found with ID: " + allocationId));
        allocationRepository.delete(allocation);
        auditService.logAction("DELETE_ALLOCATION", "Allocation", allocationId, "RESOURCE_MANAGER", "Deleted allocation");
    }
}
