package com.erasm.core;

import com.erasm.core.entity.Employee;
import com.erasm.core.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ErasmSystemHardeningIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testRegisterUser_CaseInsensitiveRole() throws Exception {
        // Test registering with lowercase role name without prefix (e.g. "employee")
        Map<String, Object> request = new HashMap<>();
        request.put("fullName", "Jane Employee");
        request.put("email", "jane.employee@erasm.com");
        request.put("password", "secure123");
        request.put("role", "employee");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Test registering with mixed-case role name with prefix (e.g. "Role_Admin")
        Map<String, Object> requestAdmin = new HashMap<>();
        requestAdmin.put("fullName", "John Admin");
        requestAdmin.put("email", "john.admin@erasm.com");
        requestAdmin.put("password", "secure123");
        requestAdmin.put("role", "Role_Admin");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAdmin)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin@erasm.com", roles = {"ADMIN", "RESOURCE_MANAGER", "DELIVERY_MANAGER"})
    void testEmployeeSkill_CaseInsensitiveEnum() throws Exception {
        // Register an employee first to assign skills
        Map<String, Object> regRequest = new HashMap<>();
        regRequest.put("fullName", "Test Emp");
        regRequest.put("email", "test.emp@erasm.com");
        regRequest.put("password", "secure123");
        regRequest.put("role", "employee");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        Employee emp = employeeRepository.findByUserEmail("test.emp@erasm.com")
                .orElseThrow(() -> new AssertionError("Employee profile not created"));

        // Call employee skills with mixed case SkillLevel e.g. "Expert"
        Map<String, Object> skillReq = new HashMap<>();
        skillReq.put("employeeId", emp.getEmployeeId());
        skillReq.put("skillId", 1L); // Java
        skillReq.put("skillLevel", "Expert");
        skillReq.put("experienceYears", 5.0);

        mockMvc.perform(post("/api/employee-skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.skillLevel").value("EXPERT"));
    }

    @Test
    @WithMockUser(username = "admin@erasm.com", roles = {"ADMIN", "RESOURCE_MANAGER", "DELIVERY_MANAGER"})
    void testEmployeeSkill_InvalidEnumMessage() throws Exception {
        Map<String, Object> skillReq = new HashMap<>();
        skillReq.put("employeeId", 2L);
        skillReq.put("skillId", 1L);
        skillReq.put("skillLevel", "super_expert"); // Invalid enum value
        skillReq.put("experienceYears", 5.0);

        mockMvc.perform(post("/api/employee-skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Allowed values: [FRESHER, BEGINNER, INTERMEDIATE, ADVANCED, EXPERT]")));
    }

    @Test
    @WithMockUser(username = "admin@erasm.com", roles = {"ADMIN", "RESOURCE_MANAGER", "DELIVERY_MANAGER"})
    void testProjectStatus_CaseInsensitiveEnum() throws Exception {
        // Create project
        Map<String, Object> projectReq = new HashMap<>();
        projectReq.put("projectName", "Project Alpha");
        projectReq.put("clientName", "Acme Corp");
        projectReq.put("startDate", "2026-07-01");
        projectReq.put("endDate", "2026-12-31");
        projectReq.put("technologyStack", "Java, Angular");
        projectReq.put("budget", 150000.0);
        projectReq.put("projectStatus", "planning"); // Case insensitive

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectStatus").value("PLANNING"));
    }

    @Test
    @WithMockUser(username = "admin@erasm.com", roles = {"ADMIN", "RESOURCE_MANAGER", "DELIVERY_MANAGER"})
    void testProjectStatusTransition_Invalid() throws Exception {
        // Create project
        Map<String, Object> projectReq = new HashMap<>();
        projectReq.put("projectName", "Project Alpha");
        projectReq.put("clientName", "Acme Corp");
        projectReq.put("startDate", "2026-07-01");
        projectReq.put("endDate", "2026-12-31");
        projectReq.put("technologyStack", "Java, Angular");
        projectReq.put("budget", 150000.0);
        projectReq.put("projectStatus", "PLANNING");

        String responseStr = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract ID
        Map<?, ?> responseMap = objectMapper.readValue(responseStr, Map.class);
        Map<?, ?> dataMap = (Map<?, ?>) responseMap.get("data");
        Number projectId = (Number) dataMap.get("projectId");

        // Try invalid transition from PLANNING to COMPLETED directly
        Map<String, Object> updateReq = new HashMap<>(projectReq);
        updateReq.put("projectStatus", "COMPLETED");

        mockMvc.perform(put("/api/projects/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid status transition from PLANNING to COMPLETED"));
    }

    @Test
    @WithMockUser(username = "admin@erasm.com", roles = {"ADMIN", "RESOURCE_MANAGER", "DELIVERY_MANAGER"})
    void testAllocation_InvalidDates() throws Exception {
        // Register employee
        Map<String, Object> regRequest = new HashMap<>();
        regRequest.put("fullName", "Test Emp Date");
        regRequest.put("email", "test.emp.date@erasm.com");
        regRequest.put("password", "secure123");
        regRequest.put("role", "employee");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        Employee emp = employeeRepository.findByUserEmail("test.emp.date@erasm.com")
                .orElseThrow(() -> new AssertionError("Employee profile not created"));

        // Create project
        Map<String, Object> projectReq = new HashMap<>();
        projectReq.put("projectName", "Project Beta");
        projectReq.put("clientName", "Acme Corp");
        projectReq.put("startDate", "2026-07-01");
        projectReq.put("endDate", "2026-12-31");
        projectReq.put("technologyStack", "Java, Angular");
        projectReq.put("budget", 150000.0);
        projectReq.put("projectStatus", "PLANNED");

        String responseStr = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> responseMap = objectMapper.readValue(responseStr, Map.class);
        Map<?, ?> dataMap = (Map<?, ?>) responseMap.get("data");
        Number projectId = (Number) dataMap.get("projectId");

        // Try allocating with startDate after endDate
        Map<String, Object> allocReq = new HashMap<>();
        allocReq.put("employeeId", emp.getEmployeeId());
        allocReq.put("projectId", projectId.longValue());
        allocReq.put("allocationPercentage", 50.0);
        allocReq.put("startDate", "2026-12-31");
        allocReq.put("endDate", "2026-07-01");
        allocReq.put("status", "ACTIVE");

        mockMvc.perform(post("/api/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Allocation failed. Start date must be strictly before end date."));
    }
}
