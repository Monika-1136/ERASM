package com.erasm.core.controller;

import com.erasm.core.dto.request.ProjectRequest;
import com.erasm.core.dto.response.ProjectResponse;
import com.erasm.core.enums.ProjectStatus;
import com.erasm.core.exception.GlobalExceptionHandler;
import com.erasm.core.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ProjectRequest projectRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        projectRequest = new ProjectRequest();
        projectRequest.setProjectName("Healthcare Portal");
        projectRequest.setClientName("Global Health Inc");
        projectRequest.setTechnologyStack("Java");
        projectRequest.setBudget(BigDecimal.valueOf(50000));
        projectRequest.setProjectStatus(ProjectStatus.IN_PROGRESS);

        projectResponse = new ProjectResponse();
        projectResponse.setProjectId(1L);
        projectResponse.setProjectName("Healthcare Portal");
        projectResponse.setClientName("Global Health Inc");
        projectResponse.setTechnologyStack("Java");
        projectResponse.setBudget(BigDecimal.valueOf(50000));
        projectResponse.setProjectStatus(ProjectStatus.IN_PROGRESS);
    }

    @Test
    void testCreateProject_Success() throws Exception {
        when(projectService.createProject(any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value(1L));

        verify(projectService).createProject(any(ProjectRequest.class));
    }

    @Test
    void testGetProjectById_Success() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value(1L));
    }

    @Test
    void testGetAllProjects_Success() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Collections.singletonList(projectResponse));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].projectId").value(1L));
    }

    @Test
    void testUpdateProject_Success() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value(1L));
    }

    @Test
    void testCloseProject_Success() throws Exception {
        when(projectService.closeProject(1L)).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
