package com.erasm.core.service.impl;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.SkillResponse;
import com.erasm.core.entity.Skill;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.mapper.SkillMapper;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SkillServiceImpl skillService;

    private Skill skill;
    private SkillRequest skillRequest;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("Java");
        skill.setCategory("Backend");
        skill.setDescription("Core Java");

        skillRequest = new SkillRequest();
        skillRequest.setSkillName("Java");
        skillRequest.setCategory("Backend");
        skillRequest.setDescription("Core Java");
    }

    @Test
    void testAddSkill_Success() {
        when(skillRepository.existsBySkillName("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillRequest)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        
        SkillResponse mockResponse = new SkillResponse();
        mockResponse.setSkillId(1L);
        mockResponse.setSkillName("Java");
        when(skillMapper.toResponse(skill)).thenReturn(mockResponse);

        SkillResponse response = skillService.addSkill(skillRequest);

        assertNotNull(response);
        assertEquals(1L, response.getSkillId());
        verify(skillRepository).save(skill);
        verify(auditService).logAction(eq("ADD_SKILL"), eq("Skill"), eq(1L), eq("ADMIN"), anyString());
    }

    @Test
    void testAddSkill_Duplicate() {
        when(skillRepository.existsBySkillName("Java")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> skillService.addSkill(skillRequest));
    }

    @Test
    void testGetSkillById_Success() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        
        SkillResponse mockResponse = new SkillResponse();
        mockResponse.setSkillId(1L);
        when(skillMapper.toResponse(skill)).thenReturn(mockResponse);

        SkillResponse response = skillService.getSkillById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getSkillId());
    }

    @Test
    void testGetSkillById_NotFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.getSkillById(1L));
    }

    @Test
    void testGetAllSkills_Success() {
        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        
        SkillResponse mockResponse = new SkillResponse();
        mockResponse.setSkillId(1L);
        when(skillMapper.toResponse(skill)).thenReturn(mockResponse);

        List<SkillResponse> list = skillService.getAllSkills();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateSkill_Success() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        
        SkillResponse mockResponse = new SkillResponse();
        mockResponse.setSkillId(1L);
        when(skillMapper.toResponse(skill)).thenReturn(mockResponse);

        SkillResponse response = skillService.updateSkill(1L, skillRequest);

        assertNotNull(response);
        verify(skillRepository).save(skill);
        verify(auditService).logAction(eq("UPDATE_SKILL"), eq("Skill"), eq(1L), eq("ADMIN"), anyString());
    }

    @Test
    void testUpdateSkill_DuplicateName() {
        skillRequest.setSkillName("React");
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.existsBySkillName("React")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> skillService.updateSkill(1L, skillRequest));
    }

    @Test
    void testDeleteSkill_Success() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.deleteSkill(1L);

        verify(skillRepository).delete(skill);
        verify(auditService).logAction(eq("DELETE_SKILL"), eq("Skill"), eq(1L), eq("ADMIN"), anyString());
    }
}
