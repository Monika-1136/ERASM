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
import com.erasm.core.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final AllocationRepository allocationRepository;
    private final ProjectMapper projectMapper;
    private final AuditService auditService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                               AllocationRepository allocationRepository,
                               ProjectMapper projectMapper,
                               AuditService auditService) {
        this.projectRepository = projectRepository;
        this.allocationRepository = allocationRepository;
        this.projectMapper = projectMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        logger.info("Creating new project: {}", request.getProjectName());
        Project project = projectMapper.toEntity(request);
        Project saved = projectRepository.save(project);
        auditService.logAction("CREATE_PROJECT", "Project", saved.getProjectId(), "DELIVERY_MANAGER", "Created project " + saved.getProjectName());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        logger.info("Updating project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));

        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setTechnologyStack(request.getTechnologyStack());
        project.setBudget(request.getBudget());
        project.setProjectStatus(request.getProjectStatus());

        Project updated = projectRepository.save(project);
        auditService.logAction("UPDATE_PROJECT", "Project", updated.getProjectId(), "DELIVERY_MANAGER", "Updated project " + updated.getProjectName());
        return projectMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProjectResponse closeProject(Long id) {
        logger.info("Closing project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));

        project.setProjectStatus(ProjectStatus.CLOSED);
        Project saved = projectRepository.save(project);

        List<Allocation> allocations = allocationRepository.findByProjectProjectId(id);
        for (Allocation alloc : allocations) {
            if (alloc.getStatus() == AllocationStatus.ACTIVE) {
                alloc.setStatus(AllocationStatus.RELEASED);
                allocationRepository.save(alloc);
            }
        }

        auditService.logAction("CLOSE_PROJECT", "Project", id, "DELIVERY_MANAGER", "Closed project and released allocations");
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
        projectRepository.delete(project);
        auditService.logAction("DELETE_PROJECT", "Project", id, "DELIVERY_MANAGER", "Deleted project ID " + id);
    }
}
