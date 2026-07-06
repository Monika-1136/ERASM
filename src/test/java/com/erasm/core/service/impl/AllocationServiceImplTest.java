package com.erasm.core.service.impl;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.entity.Allocation;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.Project;
import com.erasm.core.entity.User;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.exception.AllocationException;
import com.erasm.core.exception.ProjectNotFoundException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.mapper.AllocationMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AllocationServiceImplTest {

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AllocationMapper allocationMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AllocationServiceImpl allocationService;

    private Employee employee;
    private Project project;
    private Allocation allocation;
    private AllocationRequest allocationRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("emp@erasm.com");

        employee = new Employee();
        employee.setEmployeeId(10L);
        employee.setUser(user);

        project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Healthcare Portal");

        allocation = new Allocation();
        allocation.setAllocationId(50L);
        allocation.setEmployee(employee);
        allocation.setProject(project);
        allocation.setAllocationPercentage(60.0);
        allocation.setStatus(AllocationStatus.ACTIVE);

        allocationRequest = new AllocationRequest();
        allocationRequest.setEmployeeId(10L);
        allocationRequest.setProjectId(1L);
        allocationRequest.setAllocationPercentage(40.0);
        allocationRequest.setStartDate(LocalDate.now());
        allocationRequest.setEndDate(LocalDate.now().plusMonths(3));
        allocationRequest.setStatus(AllocationStatus.ACTIVE);
    }

    @Test
    void testAllocateEmployee_Success() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(40.0);
        when(allocationRepository.save(any(Allocation.class))).thenReturn(allocation);
        
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        AllocationResponse response = allocationService.allocateEmployee(allocationRequest);

        assertNotNull(response);
        assertEquals(50L, response.getAllocationId());
        verify(employeeRepository).findById(10L);
        verify(projectRepository).findById(1L);
        verify(allocationRepository).save(any(Allocation.class));
        verify(auditService).logAction(eq("ALLOCATE_EMPLOYEE"), eq("Allocation"), eq(50L), eq("RESOURCE_MANAGER"), anyString());
    }

    @Test
    void testAllocateEmployee_ExceedCap() {
        allocationRequest.setAllocationPercentage(70.0);
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(40.0);

        assertThrows(AllocationException.class, () -> allocationService.allocateEmployee(allocationRequest));
    }

    @Test
    void testAllocateEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> allocationService.allocateEmployee(allocationRequest));
    }

    @Test
    void testAllocateEmployee_ProjectNotFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> allocationService.allocateEmployee(allocationRequest));
    }

    @Test
    void testReallocateEmployee_Success() {
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(60.0);
        when(allocationRepository.save(any(Allocation.class))).thenReturn(allocation);

        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        // Current allocation is 60.0. Changing to 80.0.
        // sumAllocationPercentageByEmployeeAndStatusIn returns 60.0.
        // The validator will subtract the existing 60.0, leaving 0.0 + new 80.0 = 80.0 <= 100.0.
        AllocationResponse response = allocationService.reallocateEmployee(50L, 80.0);

        assertNotNull(response);
        assertEquals(80.0, allocation.getAllocationPercentage());
        verify(allocationRepository).save(allocation);
        verify(auditService).logAction(eq("REALLOCATE_EMPLOYEE"), eq("Allocation"), eq(50L), eq("RESOURCE_MANAGER"), anyString());
    }

    @Test
    void testReallocateEmployee_ExceedCap() {
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(60.0);

        assertThrows(AllocationException.class, () -> allocationService.reallocateEmployee(50L, 110.0));
    }

    @Test
    void testReleaseEmployee_Success() {
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.save(any(Allocation.class))).thenReturn(allocation);
        
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        AllocationResponse response = allocationService.releaseEmployee(50L);

        assertNotNull(response);
        assertEquals(AllocationStatus.RELEASED, allocation.getStatus());
        verify(auditService).logAction(eq("RELEASE_EMPLOYEE"), eq("Allocation"), eq(50L), eq("RESOURCE_MANAGER"), anyString());
    }

    @Test
    void testUpdateAllocationStatus_Success() {
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.save(any(Allocation.class))).thenReturn(allocation);
        
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        AllocationResponse response = allocationService.updateAllocationStatus(50L, AllocationStatus.RELEASED);

        assertNotNull(response);
        assertEquals(AllocationStatus.RELEASED, allocation.getStatus());
        verify(auditService).logAction(eq("UPDATE_ALLOCATION_STATUS"), eq("Allocation"), eq(50L), eq("RESOURCE_MANAGER"), anyString());
    }

    @Test
    void testGetLocationsByEmployee_Success() {
        when(allocationRepository.findByEmployeeEmployeeId(10L)).thenReturn(Collections.singletonList(allocation));
        
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        List<AllocationResponse> list = allocationService.getAllocationsByEmployee(10L);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testGetLocationsByProject_Success() {
        when(allocationRepository.findByProjectProjectId(1L)).thenReturn(Collections.singletonList(allocation));
        
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        List<AllocationResponse> list = allocationService.getAllocationsByProject(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testGetAllocationById_Success() {
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        AllocationResponse response = allocationService.getAllocationById(50L);
        assertNotNull(response);
        assertEquals(50L, response.getAllocationId());
    }

    @Test
    void testGetAllocationById_NotFound() {
        when(allocationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(AllocationException.class, () -> allocationService.getAllocationById(999L));
    }

    @Test
    void testGetAllAllocations_Success() {
        when(allocationRepository.findAll()).thenReturn(Collections.singletonList(allocation));
        AllocationResponse mockResponse = new AllocationResponse();
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        List<AllocationResponse> list = allocationService.getAllAllocations();
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testReallocateEmployee_NotFound() {
        when(allocationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(AllocationException.class, () -> allocationService.reallocateEmployee(999L, 50.0));
    }

    @Test
    void testReleaseEmployee_NotFound() {
        when(allocationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(AllocationException.class, () -> allocationService.releaseEmployee(999L));
    }

    @Test
    void testUpdateAllocationStatus_NotFound() {
        when(allocationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(AllocationException.class,
                () -> allocationService.updateAllocationStatus(999L, AllocationStatus.RELEASED));
    }

    @Test
    void testAllocateEmployee_NullSumFromRepository() {
        // When no existing allocations, repository returns null — should default to 0.0
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(null);
        when(allocationRepository.save(any(Allocation.class))).thenReturn(allocation);

        AllocationResponse mockResponse = new AllocationResponse();
        mockResponse.setAllocationId(50L);
        when(allocationMapper.toResponse(allocation)).thenReturn(mockResponse);

        AllocationResponse response = allocationService.allocateEmployee(allocationRequest);
        assertNotNull(response);
    }

    @Test
    void testReallocate_ExistingAllocationIsReleased_NotSubtracted() {
        // When existing allocation is RELEASED, its percentage should NOT be subtracted from cap
        allocation.setStatus(AllocationStatus.RELEASED);
        when(allocationRepository.findById(50L)).thenReturn(Optional.of(allocation));
        // Total is 60.0, but RELEASED allocation won't be subtracted → 60.0 + 50.0 = 110.0 > 100.0
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(60.0);

        assertThrows(AllocationException.class, () -> allocationService.reallocateEmployee(50L, 50.0));
    }
}
