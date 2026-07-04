package com.erasm.core.service.impl;

import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.EmployeeSkill;
import com.erasm.core.entity.User;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.mapper.EmployeeMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.EmployeeSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceAvailableTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeSkillRepository employeeSkillRepository;

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee emp1;
    private Employee emp2;
    private Employee emp3;

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setEmail("u1@erasm.com");
        u1.setFullName("Emp One");
        emp1 = new Employee();
        emp1.setEmployeeId(1L);
        emp1.setDepartment("Engineering");
        emp1.setExperienceYears(5.0);
        emp1.setUser(u1);

        User u2 = new User();
        u2.setEmail("u2@erasm.com");
        u2.setFullName("Emp Two");
        emp2 = new Employee();
        emp2.setEmployeeId(2L);
        emp2.setDepartment("Marketing");
        emp2.setExperienceYears(3.0);
        emp2.setUser(u2);

        User u3 = new User();
        u3.setEmail("u3@erasm.com");
        u3.setFullName("Emp Three");
        emp3 = new Employee();
        emp3.setEmployeeId(3L);
        emp3.setDepartment(null);
        emp3.setExperienceYears(null);
        emp3.setUser(u3);
    }

    @Test
    void testGetAvailableEmployees_AllFiltersMatch() {
        // Grouped allocations mock
        List<Object[]> allocations = Arrays.asList(
                new Object[]{1L, 40.0},
                new Object[]{2L, 80.0}
        );
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(allocations);
        when(employeeRepository.findAllWithUser()).thenReturn(Arrays.asList(emp1, emp2, emp3));

        EmployeeResponse res1 = new EmployeeResponse();
        res1.setEmployeeId(1L);
        when(employeeMapper.toResponse(eq(emp1), eq(40.0))).thenReturn(res1);

        // Filter by Engineering, minExp=4.0, maxAllocation=50.0
        List<EmployeeResponse> result = employeeService.getAvailableEmployees(null, "Engineering", 4.0, 50.0);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEmployeeId());
    }

    @Test
    void testGetAvailableEmployees_WithSkillId() {
        List<Object[]> allocations = Collections.emptyList();
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(allocations);

        EmployeeSkill es = new EmployeeSkill();
        es.setEmployee(emp1);
        when(employeeSkillRepository.findBySkillSkillId(10L)).thenReturn(Collections.singletonList(es));

        EmployeeResponse res1 = new EmployeeResponse();
        res1.setEmployeeId(1L);
        when(employeeMapper.toResponse(eq(emp1), eq(0.0))).thenReturn(res1);

        List<EmployeeResponse> result = employeeService.getAvailableEmployees(10L, null, null, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEmployeeId());
    }

    @Test
    void testGetAvailableEmployees_NullDepartmentAndExperience_Excludes() {
        List<Object[]> allocations = Collections.emptyList();
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(allocations);
        when(employeeRepository.findAllWithUser()).thenReturn(Arrays.asList(emp1, emp2, emp3));

        // Filter by Sales, or minExp=1.0 -> should exclude emp3 which has null values
        List<EmployeeResponse> resultDep = employeeService.getAvailableEmployees(null, "Sales", null, null);
        assertTrue(resultDep.isEmpty());

        List<EmployeeResponse> resultExp = employeeService.getAvailableEmployees(null, null, 1.0, null);
        // Should only return emp1 and emp2
        EmployeeResponse res1 = new EmployeeResponse();
        EmployeeResponse res2 = new EmployeeResponse();
        when(employeeMapper.toResponse(eq(emp1), eq(0.0))).thenReturn(res1);
        when(employeeMapper.toResponse(eq(emp2), eq(0.0))).thenReturn(res2);

        List<EmployeeResponse> resultExpActual = employeeService.getAvailableEmployees(null, null, 1.0, null);
        assertEquals(2, resultExpActual.size());
    }
}
