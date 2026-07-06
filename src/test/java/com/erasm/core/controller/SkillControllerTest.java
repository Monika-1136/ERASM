package com.erasm.core.controller;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.SkillResponse;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.service.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.erasm.core.exception.GlobalExceptionHandler;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link SkillController}.
 * Uses standalone MockMvc to isolate the controller layer.
 */
@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private SkillResponse skillResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        skillResponse = new SkillResponse();
        skillResponse.setSkillId(1L);
        skillResponse.setSkillName("Java");
        skillResponse.setCategory("Backend");
        skillResponse.setDescription("Java programming language");
    }

    // ========================= POST /api/skills =========================

    @Test
    void testAddSkill_Success() throws Exception {
        when(skillService.addSkill(any(SkillRequest.class))).thenReturn(skillResponse);

        String requestJson = "{\"skillName\":\"Java\",\"category\":\"Backend\",\"description\":\"Java programming language\"}";

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Skill added successfully"))
                .andExpect(jsonPath("$.data.skillId").value(1L))
                .andExpect(jsonPath("$.data.skillName").value("Java"));

        verify(skillService).addSkill(any(SkillRequest.class));
    }

    // ========================= GET /api/skills/{id} =========================

    @Test
    void testGetSkillById_Success() throws Exception {
        when(skillService.getSkillById(1L)).thenReturn(skillResponse);

        mockMvc.perform(get("/api/skills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.skillId").value(1L))
                .andExpect(jsonPath("$.data.skillName").value("Java"));

        verify(skillService).getSkillById(1L);
    }

    @Test
    void testGetSkillById_NotFound() throws Exception {
        when(skillService.getSkillById(99L)).thenThrow(new SkillNotFoundException("Skill not found with ID: 99"));

        mockMvc.perform(get("/api/skills/99"))
                .andExpect(status().isNotFound());

        verify(skillService).getSkillById(99L);
    }

    // ========================= GET /api/skills =========================

    @Test
    void testGetAllSkills_Success() throws Exception {
        SkillResponse skill2 = new SkillResponse();
        skill2.setSkillId(2L);
        skill2.setSkillName("Python");
        skill2.setCategory("Backend");

        when(skillService.getAllSkills()).thenReturn(Arrays.asList(skillResponse, skill2));

        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].skillName").value("Java"))
                .andExpect(jsonPath("$.data[1].skillName").value("Python"));

        verify(skillService).getAllSkills();
    }

    @Test
    void testGetAllSkills_EmptyList() throws Exception {
        when(skillService.getAllSkills()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ========================= PUT /api/skills/{id} =========================

    @Test
    void testUpdateSkill_Success() throws Exception {
        SkillResponse updatedResponse = new SkillResponse();
        updatedResponse.setSkillId(1L);
        updatedResponse.setSkillName("Java Advanced");
        updatedResponse.setCategory("Backend");

        when(skillService.updateSkill(eq(1L), any(SkillRequest.class))).thenReturn(updatedResponse);

        String requestJson = "{\"skillName\":\"Java Advanced\",\"category\":\"Backend\",\"description\":\"Advanced Java\"}";

        mockMvc.perform(put("/api/skills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.skillName").value("Java Advanced"));

        verify(skillService).updateSkill(eq(1L), any(SkillRequest.class));
    }

    @Test
    void testUpdateSkill_NotFound() throws Exception {
        when(skillService.updateSkill(eq(99L), any(SkillRequest.class)))
                .thenThrow(new SkillNotFoundException("Skill not found with ID: 99"));

        String requestJson = "{\"skillName\":\"Java\",\"category\":\"Backend\",\"description\":\"Java\"}";

        mockMvc.perform(put("/api/skills/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

        verify(skillService).updateSkill(eq(99L), any(SkillRequest.class));
    }

    // ========================= DELETE /api/skills/{id} =========================

    @Test
    void testDeleteSkill_Success() throws Exception {
        doNothing().when(skillService).deleteSkill(1L);

        mockMvc.perform(delete("/api/skills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Skill deleted successfully"));

        verify(skillService).deleteSkill(1L);
    }

    @Test
    void testDeleteSkill_NotFound() throws Exception {
        doThrow(new SkillNotFoundException("Skill not found with ID: 99"))
                .when(skillService).deleteSkill(99L);

        mockMvc.perform(delete("/api/skills/99"))
                .andExpect(status().isNotFound());

        verify(skillService).deleteSkill(99L);
    }
}
