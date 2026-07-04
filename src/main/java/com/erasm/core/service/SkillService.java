package com.erasm.core.service;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.SkillResponse;
import java.util.List;

public interface SkillService {
    SkillResponse addSkill(SkillRequest request);
    SkillResponse getSkillById(Long id);
    List<SkillResponse> getAllSkills();
    SkillResponse updateSkill(Long id, SkillRequest request);
    void deleteSkill(Long id);
}
