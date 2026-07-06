package com.erasm.core.repository;

import com.erasm.core.entity.ResourceRequest;
import com.erasm.core.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByProjectProjectId(Long projectId);
    List<ResourceRequest> findByStatus(RequestStatus status);
}
