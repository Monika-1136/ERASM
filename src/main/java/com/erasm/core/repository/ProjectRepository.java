package com.erasm.core.repository;

import com.erasm.core.entity.Project;
import com.erasm.core.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectStatus(ProjectStatus projectStatus);
}
