package com.erasm.core.controller;

import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.enums.SkillLevel;
import com.erasm.core.exception.GlobalExceptionHandler;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EmployeeSkillControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeSkillController employeeSkillController;

    private EmployeeSkillResponse skillResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeSkillController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        skillResponse = new EmployeeSkillResponse(1L, 5L, "Java", SkillLevel.ADVANCED, 3.0);
    }

    @Test
    void testAddOrUpdateSkill_Success() throws Exception {
        when(employeeService.addOrUpdateSkill(eq(1L), any(EmployeeSkillRequest.class))).thenReturn(skillResponse);
        String json = "{\"employeeId\":1,\"skillId\":5,\"skillLevel\":\"ADVANCED\",\"experienceYears\":3.0}";
        mockMvc.perform(post("/api/employee-skills").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.skillName").value("Java"));
        verify(employeeService).addOrUpdateSkill(eq(1L), any(EmployeeSkillRequest.class));
    }

    @Test
    void testAddOrUpdateSkill_EmployeeNotFound() throws Exception {
        when(employeeService.addOrUpdateSkill(eq(99L), any(EmployeeSkillRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        String json = "{\"employeeId\":99,\"skillId\":5,\"skillLevel\":\"ADVANCED\",\"experienceYears\":1.0}";
        mockMvc.perform(post("/api/employee-skills").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }
}
