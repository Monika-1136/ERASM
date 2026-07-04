package com.erasm.core.service.impl;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.request.EmployeeRequest;
import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.entity.*;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.enums.SkillLevel;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.exception.UserNotFoundException;
import com.erasm.core.mapper.EmployeeMapper;
import com.erasm.core.repository.*;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeSkillRepository employeeSkillRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private User user;
    private Employee employee;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("emp@erasm.com");
        user.setFullName("John Doe");

        employee = new Employee();
        employee.setEmployeeId(10L);
        employee.setUser(user);
        employee.setDepartment("IT");
        employee.setDesignation("Software Engineer");
        employee.setExperienceYears(3.5);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setUserId(1L);
        employeeRequest.setDepartment("IT");
        employeeRequest.setDesignation("Senior Software Engineer");
        employeeRequest.setExperienceYears(5.0);
    }

    @AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    private void mockUserAuthentication(String email, boolean isAdmin) {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        
        List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities;
        if (isAdmin) {
            authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        }
        doReturn(authorities).when(auth).getAuthorities();

        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateEmployee_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setEmployeeId(10L);
        when(employeeMapper.toResponse(any(Employee.class), eq(0.0))).thenReturn(mockResponse);

        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        assertNotNull(response);
        assertEquals(10L, response.getEmployeeId());
        verify(userRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
        verify(auditService).logAction(eq("CREATE_EMPLOYEE"), eq("Employee"), eq(10L), eq("emp@erasm.com"), anyString());
    }

    @Test
    void testCreateEmployee_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> employeeService.createEmployee(employeeRequest));
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(anyLong(), anyList())).thenReturn(40.0);
        
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setEmployeeId(10L);
        mockResponse.setTotalAllocationPercentage(40.0);
        when(employeeMapper.toResponse(employee, 40.0)).thenReturn(mockResponse);

        EmployeeResponse response = employeeService.getEmployeeById(10L);

        assertNotNull(response);
        assertEquals(40.0, response.getTotalAllocationPercentage());
        verify(employeeRepository).findById(10L);
        verify(allocationRepository).sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(10L));
    }

    @Test
    void testGetEmployeeByUserId_Success() {
        when(employeeRepository.findByUserUserId(1L)).thenReturn(Optional.of(employee));
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(60.0);
        
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setEmployeeId(10L);
        mockResponse.setTotalAllocationPercentage(60.0);
        when(employeeMapper.toResponse(employee, 60.0)).thenReturn(mockResponse);

        EmployeeResponse response = employeeService.getEmployeeByUserId(1L);

        assertNotNull(response);
        assertEquals(10L, response.getEmployeeId());
        assertEquals(60.0, response.getTotalAllocationPercentage());
    }

    @Test
    void testGetEmployeeByUserId_NotFound() {
        when(employeeRepository.findByUserUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeByUserId(1L));
    }

    @Test
    void testGetAllEmployees_Success() {
        when(employeeRepository.findAllWithUser()).thenReturn(Collections.singletonList(employee));
        when(allocationRepository.sumAllocationPercentageGroupedByEmployee(any())).thenReturn(new ArrayList<>());
        
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setEmployeeId(10L);
        when(employeeMapper.toResponse(employee, 0.0)).thenReturn(mockResponse);

        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateEmployee_Success() {
        mockUserAuthentication("emp@erasm.com", false);
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(eq(10L), anyList())).thenReturn(0.0);
        
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setEmployeeId(10L);
        when(employeeMapper.toResponse(employee, 0.0)).thenReturn(mockResponse);

        EmployeeResponse response = employeeService.updateEmployee(10L, employeeRequest);

        assertNotNull(response);
        verify(employeeRepository).save(employee);
        verify(auditService).logAction(eq("UPDATE_EMPLOYEE"), eq("Employee"), eq(10L), eq("emp@erasm.com"), anyString());
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(10L, employeeRequest));
    }

    @Test
    void testUpdateEmployee_AccessDenied() {
        mockUserAuthentication("other@erasm.com", false);
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> employeeService.updateEmployee(10L, employeeRequest));
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(10L);

        verify(employeeRepository).delete(employee);
        verify(auditService).logAction(eq("DELETE_EMPLOYEE"), eq("Employee"), eq(10L), eq("ADMIN"), anyString());
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(10L));
    }

    @Test
    void testAddOrUpdateSkill_NewSkill() {
        mockUserAuthentication("emp@erasm.com", false);
        EmployeeSkillRequest skillRequest = new EmployeeSkillRequest();
        skillRequest.setSkillId(20L);
        skillRequest.setSkillLevel(SkillLevel.INTERMEDIATE);
        skillRequest.setExperienceYears(2.0);

        Skill skill = new Skill();
        skill.setSkillId(20L);
        skill.setSkillName("Java");

        EmployeeSkill employeeSkill = new EmployeeSkill();
        employeeSkill.setEmployeeSkillId(5L);
        employeeSkill.setEmployee(employee);
        employeeSkill.setSkill(skill);
        employeeSkill.setSkillLevel(SkillLevel.INTERMEDIATE);
        employeeSkill.setExperienceYears(2.0);

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(skillRepository.findById(20L)).thenReturn(Optional.of(skill));
        when(employeeSkillRepository.findByEmployeeEmployeeIdAndSkillSkillId(10L, 20L)).thenReturn(Optional.empty());
        when(employeeSkillRepository.save(any(EmployeeSkill.class))).thenReturn(employeeSkill);

        EmployeeSkillResponse response = employeeService.addOrUpdateSkill(10L, skillRequest);

        assertNotNull(response);
        assertEquals(5L, response.getEmployeeSkillId());
        assertEquals("Java", response.getSkillName());
        assertEquals(SkillLevel.INTERMEDIATE, response.getSkillLevel());
    }

    @Test
    void testAddOrUpdateSkill_SkillNotFound() {
        mockUserAuthentication("emp@erasm.com", false);
        EmployeeSkillRequest skillRequest = new EmployeeSkillRequest();
        skillRequest.setSkillId(20L);

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(skillRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> employeeService.addOrUpdateSkill(10L, skillRequest));
    }

    @Test
    void testAddCertification_Success() {
        mockUserAuthentication("emp@erasm.com", false);
        CertificationRequest certRequest = new CertificationRequest();
        certRequest.setCertificationName("AWS Cloud Practitioner");
        certRequest.setIssuingOrganization("AWS");
        certRequest.setIssueDate(LocalDate.now());

        Certification certification = new Certification();
        certification.setCertificationId(30L);
        certification.setCertificationName("AWS Cloud Practitioner");

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(certificationRepository.save(any(Certification.class))).thenReturn(certification);

        CertificationResponse response = employeeService.addCertification(10L, certRequest);

        assertNotNull(response);
        assertEquals(30L, response.getCertificationId());
        assertEquals("AWS Cloud Practitioner", response.getCertificationName());
    }

    @Test
    void testDeleteCertification_Success() {
        mockUserAuthentication("emp@erasm.com", false);
        Certification certification = new Certification();
        certification.setCertificationId(30L);
        certification.setCertificationName("AWS Cloud Practitioner");
        certification.setEmployee(employee);

        when(certificationRepository.findById(30L)).thenReturn(Optional.of(certification));

        employeeService.deleteCertification(30L);

        verify(certificationRepository).delete(certification);
        verify(auditService).logAction(eq("DELETE_CERTIFICATION"), eq("Certification"), eq(30L), eq("emp@erasm.com"), anyString());
    }
}
