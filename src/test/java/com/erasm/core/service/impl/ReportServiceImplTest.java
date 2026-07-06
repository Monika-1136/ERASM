package com.erasm.core.service.impl;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.EmployeeSkill;
import com.erasm.core.entity.Skill;
import com.erasm.core.entity.User;
import com.erasm.core.enums.SkillLevel;
import com.erasm.core.mapper.AllocationMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.EmployeeSkillRepository;
import com.erasm.core.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeSkillRepository employeeSkillRepository;

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    @SuppressWarnings("unused")
    private AllocationMapper allocationMapper;

    @Mock
    private com.erasm.core.repository.ProjectRepository projectRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void testGetSkillReport_Success() {
        Skill skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("Java");
        skill.setCategory("Backend");

        EmployeeSkill employeeSkill = new EmployeeSkill();
        employeeSkill.setEmployeeSkillId(10L);
        employeeSkill.setSkillLevel(SkillLevel.ADVANCED);
        employeeSkill.setExperienceYears(4.0);
        employeeSkill.setSkill(skill);

        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(employeeSkillRepository.findAllWithSkillAndEmployee()).thenReturn(Collections.singletonList(employeeSkill));

        List<SkillReportResponse> report = reportService.getSkillReport();

        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("Java", report.get(0).getSkillName());
        assertEquals(1, report.get(0).getCount());
    }

    @Test
    void testGetUtilizationReport_Success() {
        User user = new User();
        user.setFullName("John Doe");

        Employee employee = new Employee();
        employee.setEmployeeId(10L);
        employee.setDepartment("IT");
        employee.setUser(user);

        List<Object[]> allocationsList = new ArrayList<>();
        allocationsList.add(new Object[]{10L, 80.0});

        when(employeeRepository.findAllWithUser()).thenReturn(Collections.singletonList(employee));
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(allocationsList);

        List<UtilizationReportResponse> report = reportService.getUtilizationReport();

        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("John Doe", report.get(0).getEmployeeName());
        assertEquals(80.0, report.get(0).getTotalAllocatedPercentage());
        assertEquals(80.0, report.get(0).getBillablePercentage());
        assertEquals(20.0, report.get(0).getBenchPercentage());
    }

    @Test
    void testGetProjectAllocationReport_Success() {
        when(allocationRepository.findAllWithEmployeeAndProject()).thenReturn(Collections.emptyList());

        List<AllocationResponse> list = reportService.getProjectAllocationReport();

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetDashboardMetrics_Success() {
        Employee emp = new Employee();
        emp.setEmployeeId(10L);

        List<Object[]> allocationsList = new ArrayList<>();
        allocationsList.add(new Object[]{10L, 120.0}); // overallocated

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(emp));
        when(projectRepository.count()).thenReturn(5L);
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(allocationsList);

        com.erasm.core.dto.response.DashboardResponse response = reportService.getDashboardMetrics();

        assertNotNull(response);
        assertEquals(5L, response.getProjectCount());
        assertEquals(1, response.getOverallocatedEmployeesCount());
        assertEquals(0, response.getBenchEmployeesCount());
        assertEquals(120.0, response.getAllocationPercentage());
    }
}
