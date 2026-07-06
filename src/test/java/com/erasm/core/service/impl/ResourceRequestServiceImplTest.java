package com.erasm.core.service.impl;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.entity.Project;
import com.erasm.core.entity.ResourceRequest;
import com.erasm.core.entity.Skill;
import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import com.erasm.core.exception.ProjectNotFoundException;
import com.erasm.core.exception.ResourceRequestException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.mapper.ResourceRequestMapper;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.repository.ResourceRequestRepository;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.erasm.core.exception.InvalidWorkflowException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceRequestServiceImplTest {

    @Mock
    private ResourceRequestRepository resourceRequestRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ResourceRequestMapper resourceRequestMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ResourceRequestServiceImpl resourceRequestService;

    private Project project;
    private Skill skill;
    private ResourceRequest resourceRequest;
    private ResourceRequestDto resourceRequestDto;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Healthcare Portal");

        skill = new Skill();
        skill.setSkillId(10L);
        skill.setSkillName("Java");

        resourceRequest = new ResourceRequest();
        resourceRequest.setRequestId(100L);
        resourceRequest.setProject(project);
        resourceRequest.setSkill(skill);
        resourceRequest.setRequiredCount(3);
        resourceRequest.setRequiredLevel(SkillLevel.ADVANCED);
        resourceRequest.setStatus(RequestStatus.SUBMITTED);

        resourceRequestDto = new ResourceRequestDto();
        resourceRequestDto.setProjectId(1L);
        resourceRequestDto.setSkillId(10L);
        resourceRequestDto.setRequiredCount(3);
        resourceRequestDto.setRequiredLevel(SkillLevel.ADVANCED);
        resourceRequestDto.setStatus(RequestStatus.SUBMITTED);
    }

    @Test
    void testCreateResourceRequest_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);
        
        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        ResourceRequestResponse response = resourceRequestService.createResourceRequest(resourceRequestDto);

        assertNotNull(response);
        assertEquals(100L, response.getRequestId());
        verify(resourceRequestRepository).save(any(ResourceRequest.class));
        verify(auditService).logAction(eq("CREATE_RESOURCE_REQUEST"), eq("ResourceRequest"), eq(100L), eq("DELIVERY_MANAGER"), anyString());
    }

    @Test
    void testCreateResourceRequest_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> resourceRequestService.createResourceRequest(resourceRequestDto));
    }

    @Test
    void testCreateResourceRequest_SkillNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> resourceRequestService.createResourceRequest(resourceRequestDto));
    }

    @Test
    void testGetRequestById_Success() {
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));
        
        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        ResourceRequestResponse response = resourceRequestService.getRequestById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getRequestId());
    }

    @Test
    void testGetRequestById_NotFound() {
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceRequestException.class, () -> resourceRequestService.getRequestById(100L));
    }

    @Test
    void testGetAllRequests_Success() {
        when(resourceRequestRepository.findAll()).thenReturn(Collections.singletonList(resourceRequest));
        
        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        List<ResourceRequestResponse> list = resourceRequestService.getAllRequests();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testGetRequestsByProject_Success() {
        when(resourceRequestRepository.findByProjectProjectId(1L)).thenReturn(Collections.singletonList(resourceRequest));
        
        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        List<ResourceRequestResponse> list = resourceRequestService.getRequestsByProject(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateRequestStatus_Success() {
        resourceRequest.setStatus(RequestStatus.RESOURCE_MANAGER_REVIEW);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);
        
        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        ResourceRequestResponse response = resourceRequestService.updateRequestStatus(100L, RequestStatus.APPROVED);

        assertNotNull(response);
        assertEquals(RequestStatus.APPROVED, resourceRequest.getStatus());
        verify(auditService).logAction(eq("UPDATE_REQUEST_STATUS"), eq("ResourceRequest"), eq(100L), eq("RESOURCE_MANAGER"), anyString());
    }

    @Test
    void testUpdateRequestStatus_InvalidTransition() {
        resourceRequest.setStatus(RequestStatus.COMPLETED);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));

        assertThrows(InvalidWorkflowException.class,
                () -> resourceRequestService.updateRequestStatus(100L, RequestStatus.DRAFT));
    }

    @Test
    void testUpdateRequestStatus_SameStatus_NoException() {
        // Transitioning to same status is allowed (no-op)
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest)); // SUBMITTED
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);

        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(resourceRequest)).thenReturn(mockResponse);

        assertDoesNotThrow(() -> resourceRequestService.updateRequestStatus(100L, RequestStatus.SUBMITTED));
    }

    @Test
    void testUpdateRequestStatus_DraftToSubmitted() {
        resourceRequest.setStatus(RequestStatus.DRAFT);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);

        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        when(resourceRequestMapper.toResponse(any())).thenReturn(mockResponse);

        assertDoesNotThrow(() -> resourceRequestService.updateRequestStatus(100L, RequestStatus.SUBMITTED));
    }

    @Test
    void testUpdateRequestStatus_ResourceManagerReviewToApproved() {
        resourceRequest.setStatus(RequestStatus.RESOURCE_MANAGER_REVIEW);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);

        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        when(resourceRequestMapper.toResponse(any())).thenReturn(mockResponse);

        assertDoesNotThrow(() -> resourceRequestService.updateRequestStatus(100L, RequestStatus.APPROVED));
    }

    @Test
    void testUpdateRequestStatus_AllocatedToInProgress() {
        resourceRequest.setStatus(RequestStatus.ALLOCATED);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);

        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        when(resourceRequestMapper.toResponse(any())).thenReturn(mockResponse);

        assertDoesNotThrow(() -> resourceRequestService.updateRequestStatus(100L, RequestStatus.IN_PROGRESS));
    }

    @Test
    void testUpdateRequestStatus_InvalidTransition_DraftToCompleted() {
        resourceRequest.setStatus(RequestStatus.DRAFT);
        when(resourceRequestRepository.findById(100L)).thenReturn(Optional.of(resourceRequest));

        assertThrows(InvalidWorkflowException.class,
                () -> resourceRequestService.updateRequestStatus(100L, RequestStatus.COMPLETED));
    }

    @Test
    void testCreateResourceRequest_WithNullStatus_DefaultsToSubmitted() {
        resourceRequestDto.setStatus(null); // null → defaults to SUBMITTED
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(resourceRequestRepository.save(any(ResourceRequest.class))).thenReturn(resourceRequest);

        ResourceRequestResponse mockResponse = new ResourceRequestResponse();
        mockResponse.setRequestId(100L);
        when(resourceRequestMapper.toResponse(any())).thenReturn(mockResponse);

        ResourceRequestResponse response = resourceRequestService.createResourceRequest(resourceRequestDto);
        assertNotNull(response);
    }
}
