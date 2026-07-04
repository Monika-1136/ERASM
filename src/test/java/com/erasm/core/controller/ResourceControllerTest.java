package com.erasm.core.controller;

import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ResourceController}.
 * Covers the resource availability endpoint with optional parameters.
 */
@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private ResourceController resourceController;

    private EmployeeResponse employee1;
    private EmployeeResponse employee2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();

        employee1 = new EmployeeResponse();
        employee1.setEmployeeId(1L);
        employee1.setFullName("Alice Smith");
        employee1.setTotalAllocationPercentage(40.0);

        employee2 = new EmployeeResponse();
        employee2.setEmployeeId(2L);
        employee2.setFullName("Bob Jones");
        employee2.setTotalAllocationPercentage(0.0);
    }

    @Test
    void testGetAvailableEmployees_NoFilters() throws Exception {
        when(employeeService.getAvailableEmployees(null, null, null, 100.0))
                .thenReturn(Arrays.asList(employee1, employee2));

        mockMvc.perform(get("/resources/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].fullName").value("Alice Smith"));

        verify(employeeService).getAvailableEmployees(null, null, null, 100.0);
    }

    @Test
    void testGetAvailableEmployees_WithSkillFilter() throws Exception {
        when(employeeService.getAvailableEmployees(5L, null, null, 100.0))
                .thenReturn(Collections.singletonList(employee1));

        mockMvc.perform(get("/resources/available").param("skillId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].employeeId").value(1L));

        verify(employeeService).getAvailableEmployees(5L, null, null, 100.0);
    }

    @Test
    void testGetAvailableEmployees_WithMaxAllocationFilter() throws Exception {
        when(employeeService.getAvailableEmployees(null, null, null, 50.0))
                .thenReturn(Arrays.asList(employee1, employee2));

        mockMvc.perform(get("/resources/available").param("maxAllocation", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(employeeService).getAvailableEmployees(null, null, null, 50.0);
    }

    @Test
    void testGetAvailableEmployees_WithBothFilters() throws Exception {
        when(employeeService.getAvailableEmployees(3L, null, null, 80.0))
                .thenReturn(Collections.singletonList(employee2));

        mockMvc.perform(get("/resources/available")
                        .param("skillId", "3")
                        .param("maxAllocation", "80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].fullName").value("Bob Jones"));

        verify(employeeService).getAvailableEmployees(3L, null, null, 80.0);
    }

    @Test
    void testGetAvailableEmployees_EmptyResult() throws Exception {
        when(employeeService.getAvailableEmployees(null, null, null, 100.0))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/resources/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
