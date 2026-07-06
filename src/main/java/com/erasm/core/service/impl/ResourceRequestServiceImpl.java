package com.erasm.core.service.impl;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.request.RequestSkillDto;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.entity.Project;
import com.erasm.core.entity.ResourceRequest;
import com.erasm.core.entity.RequestSkill;
import com.erasm.core.entity.Skill;
import com.erasm.core.enums.RequestStatus;
import com.erasm.core.exception.ProjectNotFoundException;
import com.erasm.core.exception.InvalidWorkflowException;
import com.erasm.core.exception.ResourceRequestException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.mapper.ResourceRequestMapper;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.repository.ResourceRequestRepository;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.ResourceRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceRequestServiceImpl implements ResourceRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceRequestServiceImpl.class);

    private final ResourceRequestRepository resourceRequestRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ResourceRequestMapper resourceRequestMapper;
    private final AuditService auditService;

    public ResourceRequestServiceImpl(ResourceRequestRepository resourceRequestRepository,
                                     ProjectRepository projectRepository,
                                     SkillRepository skillRepository,
                                     ResourceRequestMapper resourceRequestMapper,
                                     AuditService auditService) {
        this.resourceRequestRepository = resourceRequestRepository;
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.resourceRequestMapper = resourceRequestMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public ResourceRequestResponse createResourceRequest(ResourceRequestDto request) {
        logger.info("Creating resource request for project ID: {}", request.getProjectId());
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + request.getProjectId()));

        ResourceRequest resourceRequest = new ResourceRequest();
        resourceRequest.setProject(project);
        resourceRequest.setStatus(request.getStatus() != null ? request.getStatus() : RequestStatus.SUBMITTED);
        resourceRequest.setRequestedBy("DELIVERY_MANAGER");
        resourceRequest.setRequestDate(LocalDate.now());
        resourceRequest.setRemarks(request.getRemarks());

        List<RequestSkill> requestSkills = new ArrayList<>();
        if (request.getSkills() != null) {
            for (RequestSkillDto skillDto : request.getSkills()) {
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new SkillNotFoundException("Skill not found with ID: " + skillDto.getSkillId()));
                
                RequestSkill requestSkill = new RequestSkill();
                requestSkill.setResourceRequest(resourceRequest);
                requestSkill.setSkill(skill);
                requestSkill.setRequiredCount(skillDto.getRequiredCount());
                requestSkill.setRequiredLevel(skillDto.getRequiredLevel());
                requestSkills.add(requestSkill);
            }
        }
        resourceRequest.setRequestSkills(requestSkills);

        ResourceRequest saved = resourceRequestRepository.save(resourceRequest);
        
        // Log action in AuditLog
        auditService.logAction("CREATE_RESOURCE_REQUEST", "ResourceRequest", saved.getRequestId(), "DELIVERY_MANAGER", "Requested skills for project: " + project.getProjectName());
        
        return resourceRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceRequestResponse getRequestById(Long id) {
        ResourceRequest request = resourceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceRequestException("Resource request not found with ID: " + id));
        return resourceRequestMapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getAllRequests() {
        return resourceRequestRepository.findAll().stream()
                .map(resourceRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getRequestsByProject(Long projectId) {
        return resourceRequestRepository.findByProjectProjectId(projectId).stream()
                .map(resourceRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResourceRequestResponse updateRequestStatus(Long id, RequestStatus status) {
        logger.info("Updating status of resource request ID: {} to {}", id, status);
        ResourceRequest request = resourceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceRequestException("Resource request not found with ID: " + id));

        validateStatusTransition(request.getStatus(), status);

        request.setStatus(status);
        ResourceRequest saved = resourceRequestRepository.save(request);
        
        // Log action in AuditLog
        auditService.logAction("UPDATE_REQUEST_STATUS", "ResourceRequest", saved.getRequestId(), "RESOURCE_MANAGER", "Updated status to " + status);
        
        return resourceRequestMapper.toResponse(saved);
    }

    private void validateStatusTransition(RequestStatus current, RequestStatus target) {
        if (current == target) {
            return;
        }
        
        boolean valid = false;
        switch (current) {
            case DRAFT:
                if (target == RequestStatus.SUBMITTED) valid = true;
                break;
            case SUBMITTED:
                if (target == RequestStatus.RESOURCE_MANAGER_REVIEW) valid = true;
                break;
            case RESOURCE_MANAGER_REVIEW:
                if (target == RequestStatus.APPROVED || target == RequestStatus.REJECTED) valid = true;
                break;
            case APPROVED:
                if (target == RequestStatus.ALLOCATED) valid = true;
                break;
            case REJECTED:
                if (target == RequestStatus.DRAFT) valid = true;
                break;
            case ALLOCATED:
                if (target == RequestStatus.IN_PROGRESS) valid = true;
                break;
            case IN_PROGRESS:
                if (target == RequestStatus.COMPLETED || target == RequestStatus.CANCELLED) valid = true;
                break;
            default:
                break;
        }
        if (!valid) {
            throw new InvalidWorkflowException("Invalid status transition from " + current + " to " + target);
        }
    }
}
