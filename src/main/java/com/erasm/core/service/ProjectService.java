package com.erasm.core.service;

import com.erasm.core.dto.request.ProjectRequest;
import com.erasm.core.dto.response.ProjectResponse;
import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse getProjectById(Long id);
    List<ProjectResponse> getAllProjects();
    ProjectResponse updateProject(Long id, ProjectRequest request);
    ProjectResponse closeProject(Long id);
    void deleteProject(Long id);
}
