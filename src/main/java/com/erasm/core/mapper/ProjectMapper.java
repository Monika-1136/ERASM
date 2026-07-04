package com.erasm.core.mapper;

import com.erasm.core.dto.request.ProjectRequest;
import com.erasm.core.dto.response.ProjectResponse;
import com.erasm.core.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        if (project == null) return null;
        return new ProjectResponse(
                project.getProjectId(),
                project.getProjectName(),
                project.getClientName(),
                project.getStartDate(),
                project.getEndDate(),
                project.getTechnologyStack(),
                project.getBudget(),
                project.getProjectStatus()
        );
    }

    public Project toEntity(ProjectRequest request) {
        if (request == null) return null;
        Project project = new Project();
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setTechnologyStack(request.getTechnologyStack());
        project.setBudget(request.getBudget());
        project.setProjectStatus(request.getProjectStatus());
        return project;
    }
}
