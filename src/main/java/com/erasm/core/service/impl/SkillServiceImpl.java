package com.erasm.core.service.impl;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.SkillResponse;
import com.erasm.core.entity.Skill;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.mapper.SkillMapper;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillServiceImpl.class);

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final AuditService auditService;

    public SkillServiceImpl(SkillRepository skillRepository, SkillMapper skillMapper, AuditService auditService) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public SkillResponse addSkill(SkillRequest request) {
        logger.info("Adding new skill: {}", request.getSkillName());
        if (skillRepository.existsBySkillName(request.getSkillName())) {
            throw new DuplicateResourceException("Skill already exists: " + request.getSkillName());
        }

        Skill skill = skillMapper.toEntity(request);
        Skill saved = skillRepository.save(skill);
        auditService.logAction("ADD_SKILL", "Skill", saved.getSkillId(), "ADMIN", "Added skill " + saved.getSkillName());
        return skillMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found with ID: " + id));
        return skillMapper.toResponse(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(skillMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        logger.info("Updating skill with ID: {}", id);
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found with ID: " + id));

        if (!skill.getSkillName().equalsIgnoreCase(request.getSkillName()) && skillRepository.existsBySkillName(request.getSkillName())) {
            throw new DuplicateResourceException("Skill already exists: " + request.getSkillName());
        }

        skill.setSkillName(request.getSkillName());
        skill.setCategory(request.getCategory());
        skill.setDescription(request.getDescription());

        Skill updated = skillRepository.save(skill);
        auditService.logAction("UPDATE_SKILL", "Skill", updated.getSkillId(), "ADMIN", "Updated skill " + updated.getSkillName());
        return skillMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        logger.info("Deleting skill with ID: {}", id);
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found with ID: " + id));
        skillRepository.delete(skill);
        auditService.logAction("DELETE_SKILL", "Skill", id, "ADMIN", "Deleted skill ID " + id);
    }
}
