package com.erasm.core.mapper;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.SkillResponse;
import com.erasm.core.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public SkillResponse toResponse(Skill skill) {
        if (skill == null) return null;
        return new SkillResponse(
                skill.getSkillId(),
                skill.getSkillName(),
                skill.getCategory(),
                skill.getDescription()
        );
    }

    public Skill toEntity(SkillRequest request) {
        if (request == null) return null;
        Skill skill = new Skill();
        skill.setSkillName(request.getSkillName());
        skill.setCategory(request.getCategory());
        skill.setDescription(request.getDescription());
        return skill;
    }
}
