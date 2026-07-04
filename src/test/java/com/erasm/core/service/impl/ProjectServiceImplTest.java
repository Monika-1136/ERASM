package com.erasm.core.service.impl;

import com.erasm.core.dto.request.ProjectRequest;
import com.erasm.core.dto.response.ProjectResponse;
import com.erasm.core.entity.Allocation;
import com.erasm.core.entity.Project;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.enums.ProjectStatus;
import com.erasm.core.exception.ProjectNotFoundException;
import com.erasm.core.mapper.ProjectMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRequest projectRequest;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Healthcare Portal");
        project.setClientName("Global Health Inc");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(6));
        project.setTechnologyStack("Java, Spring Boot, React");
        project.setBudget(BigDecimal.valueOf(100000));
        project.setProjectStatus(ProjectStatus.ACTIVE);

        projectRequest = new ProjectRequest();
        projectRequest.setProjectName("Healthcare Portal");
        projectRequest.setClientName("Global Health Inc");
        projectRequest.setStartDate(LocalDate.now());
        projectRequest.setEndDate(LocalDate.now().plusMonths(6));
        projectRequest.setTechnologyStack("Java, Spring Boot, React");
        projectRequest.setBudget(BigDecimal.valueOf(100000));
        projectRequest.setProjectStatus(ProjectStatus.ACTIVE);
    }

    @Test
    void testCreateProject_Success() {
        when(projectMapper.toEntity(projectRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        
        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setProjectId(1L);
        mockResponse.setProjectName("Healthcare Portal");
        when(projectMapper.toResponse(project)).thenReturn(mockResponse);

        ProjectResponse response = projectService.createProject(projectRequest);

        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
        verify(projectRepository).save(project);
        verify(auditService).logAction(eq("CREATE_PROJECT"), eq("Project"), eq(1L), eq("DELIVERY_MANAGER"), anyString());
    }

    @Test
    void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        
        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setProjectId(1L);
        when(projectMapper.toResponse(project)).thenReturn(mockResponse);

        ProjectResponse response = projectService.getProjectById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void testGetAllProjects_Success() {
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        
        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setProjectId(1L);
        when(projectMapper.toResponse(project)).thenReturn(mockResponse);

        List<ProjectResponse> responseList = projectService.getAllProjects();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
    }

    @Test
    void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        
        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setProjectId(1L);
        when(projectMapper.toResponse(project)).thenReturn(mockResponse);

        ProjectResponse response = projectService.updateProject(1L, projectRequest);

        assertNotNull(response);
        verify(projectRepository).save(project);
        verify(auditService).logAction(eq("UPDATE_PROJECT"), eq("Project"), eq(1L), eq("DELIVERY_MANAGER"), anyString());
    }

    @Test
    void testCloseProject_Success() {
        Allocation alloc1 = new Allocation();
        alloc1.setAllocationId(10L);
        alloc1.setStatus(AllocationStatus.ACTIVE);

        Allocation alloc2 = new Allocation();
        alloc2.setAllocationId(11L);
        alloc2.setStatus(AllocationStatus.RELEASED);

        List<Allocation> mockAllocations = new ArrayList<>();
        mockAllocations.add(alloc1);
        mockAllocations.add(alloc2);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(allocationRepository.findByProjectProjectId(1L)).thenReturn(mockAllocations);

        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setProjectId(1L);
        when(projectMapper.toResponse(project)).thenReturn(mockResponse);

        ProjectResponse response = projectService.closeProject(1L);

        assertNotNull(response);
        assertEquals(ProjectStatus.CLOSED, project.getProjectStatus());
        assertEquals(AllocationStatus.RELEASED, alloc1.getStatus()); // Allocated became released
        assertEquals(AllocationStatus.RELEASED, alloc2.getStatus()); // Released stayed released
        verify(allocationRepository, times(1)).save(alloc1);
        verify(allocationRepository, never()).save(alloc2);
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        verify(projectRepository).delete(project);
        verify(auditService).logAction(eq("DELETE_PROJECT"), eq("Project"), eq(1L), eq("DELIVERY_MANAGER"), anyString());
    }
}
