package com.erasm.core.mapper;

import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.entity.ResourceRequest;
import org.springframework.stereotype.Component;

@Component
public class ResourceRequestMapper {

    public ResourceRequestResponse toResponse(ResourceRequest request) {
        if (request == null) return null;
        ResourceRequestResponse response = new ResourceRequestResponse();
        response.setRequestId(request.getRequestId());
        if (request.getProject() != null) {
            response.setProjectId(request.getProject().getProjectId());
            response.setProjectName(request.getProject().getProjectName());
        }
        if (request.getSkill() != null) {
            response.setSkillId(request.getSkill().getSkillId());
            response.setSkillName(request.getSkill().getSkillName());
        }
        response.setRequiredCount(request.getRequiredCount());
        response.setRequiredLevel(request.getRequiredLevel());
        response.setStatus(request.getStatus());
        response.setRequestedBy(request.getRequestedBy());
        response.setCreatedDate(request.getCreatedDate());
        return response;
    }
}
