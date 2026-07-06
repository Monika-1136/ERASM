package com.erasm.core.mapper;

import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.dto.response.RequestSkillResponse;
import com.erasm.core.entity.ResourceRequest;
import com.erasm.core.entity.RequestSkill;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

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
        
        List<RequestSkillResponse> skillResponses = new ArrayList<>();
        if (request.getRequestSkills() != null) {
            for (RequestSkill rs : request.getRequestSkills()) {
                RequestSkillResponse rsr = new RequestSkillResponse();
                rsr.setRequestSkillId(rs.getRequestSkillId());
                if (rs.getSkill() != null) {
                    rsr.setSkillId(rs.getSkill().getSkillId());
                    rsr.setSkillName(rs.getSkill().getSkillName());
                }
                rsr.setRequiredCount(rs.getRequiredCount());
                rsr.setRequiredLevel(rs.getRequiredLevel());
                skillResponses.add(rsr);
            }
        }
        response.setSkills(skillResponses);
        response.setStatus(request.getStatus());
        response.setRequestedBy(request.getRequestedBy());
        response.setRequestDate(request.getRequestDate());
        response.setRemarks(request.getRemarks());
        return response;
    }
}
