package com.erasm.core.controller;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.request.EmployeeRequest;
import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link EmployeeController}.
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeResponse employeeResponse;
    private EmployeeSkillResponse skillResponse;
    private CertificationResponse certResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeId(1L);
        employeeResponse.setUserId(10L);
        employeeResponse.setFullName("Alice Smith");
        employeeResponse.setEmail("alice@erasm.com");
        employeeResponse.setDepartment("Engineering");
        employeeResponse.setDesignation("Software Engineer");
        employeeResponse.setExperienceYears(3.5);
        employeeResponse.setTotalAllocationPercentage(60.0);

        skillResponse = new EmployeeSkillResponse(1L, 5L, "Java", SkillLevel.ADVANCED, 3.0);
        certResponse = new CertificationResponse(1L, "AWS Certified", "Amazon",
                LocalDate.of(2024, 1, 1), LocalDate.of(2027, 1, 1));
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);
        String json = "{\"userId\":10,\"department\":\"Engineering\",\"designation\":\"Software Engineer\"}";
        mockMvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeId").value(1L));
        verify(employeeService).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    void testGetAllEmployees_Success() throws Exception {
        EmployeeResponse emp2 = new EmployeeResponse();
        emp2.setEmployeeId(2L);
        emp2.setFullName("Bob Jones");
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(employeeResponse, emp2));
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
        verify(employeeService).getAllEmployees();
    }

    @Test
    void testGetAllEmployees_Empty() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeResponse);
        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employeeId").value(1L))
                .andExpect(jsonPath("$.data.email").value("alice@erasm.com"));
        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeeByUserId_Success() throws Exception {
        when(employeeService.getEmployeeByUserId(10L)).thenReturn(employeeResponse);
        mockMvc.perform(get("/employees/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(10L));
        verify(employeeService).getEmployeeByUserId(10L);
    }

    @Test
    void testGetEmployeeByUserId_NotFound() throws Exception {
        when(employeeService.getEmployeeByUserId(999L))
                .thenThrow(new ResourceNotFoundException("Employee profile not found for user ID: 999"));
        mockMvc.perform(get("/employees/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        EmployeeResponse updated = new EmployeeResponse();
        updated.setEmployeeId(1L);
        updated.setDesignation("Senior Engineer");
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).thenReturn(updated);
        String json = "{\"userId\":10,\"department\":\"DevOps\",\"designation\":\"Senior Engineer\"}";
        mockMvc.perform(put("/employees/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.designation").value("Senior Engineer"));
        verify(employeeService).updateEmployee(eq(1L), any(EmployeeRequest.class));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        when(employeeService.updateEmployee(eq(99L), any(EmployeeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        String json = "{\"userId\":10,\"department\":\"DevOps\",\"designation\":\"Engineer\"}";
        mockMvc.perform(put("/employees/99").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Employee not found with ID: 99")).when(employeeService).deleteEmployee(99L);
        mockMvc.perform(delete("/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddOrUpdateSkill_Success() throws Exception {
        when(employeeService.addOrUpdateSkill(eq(1L), any(EmployeeSkillRequest.class))).thenReturn(skillResponse);
        String json = "{\"skillId\":5,\"skillLevel\":\"ADVANCED\",\"experienceYears\":3.0}";
        mockMvc.perform(post("/employees/1/skills").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skillName").value("Java"));
        verify(employeeService).addOrUpdateSkill(eq(1L), any(EmployeeSkillRequest.class));
    }

    @Test
    void testAddOrUpdateSkill_EmployeeNotFound() throws Exception {
        when(employeeService.addOrUpdateSkill(eq(99L), any(EmployeeSkillRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        String json = "{\"skillId\":5,\"skillLevel\":\"ADVANCED\"}";
        mockMvc.perform(post("/employees/99/skills").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddCertification_Success() throws Exception {
        when(employeeService.addCertification(eq(1L), any(CertificationRequest.class))).thenReturn(certResponse);
        String json = "{\"certificationName\":\"AWS Certified\",\"issuingOrganization\":\"Amazon\",\"issueDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/employees/1/certifications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.certificationName").value("AWS Certified"));
        verify(employeeService).addCertification(eq(1L), any(CertificationRequest.class));
    }

    @Test
    void testAddCertification_EmployeeNotFound() throws Exception {
        when(employeeService.addCertification(eq(99L), any(CertificationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        String json = "{\"certificationName\":\"AWS Certified\",\"issuingOrganization\":\"Amazon\",\"issueDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/employees/99/certifications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }
}
