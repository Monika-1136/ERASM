package com.erasm.core.service.impl;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.request.EmployeeRequest;
import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.entity.Certification;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.EmployeeSkill;
import com.erasm.core.entity.Skill;
import com.erasm.core.entity.User;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.exception.SkillNotFoundException;
import com.erasm.core.exception.UserNotFoundException;
import com.erasm.core.mapper.EmployeeMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.CertificationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.EmployeeSkillRepository;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final CertificationRepository certificationRepository;
    private final AllocationRepository allocationRepository;
    private final EmployeeMapper employeeMapper;
    private final AuditService auditService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               UserRepository userRepository,
                               SkillRepository skillRepository,
                               EmployeeSkillRepository employeeSkillRepository,
                               CertificationRepository certificationRepository,
                               AllocationRepository allocationRepository,
                               EmployeeMapper employeeMapper,
                               AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.employeeSkillRepository = employeeSkillRepository;
        this.certificationRepository = certificationRepository;
        this.allocationRepository = allocationRepository;
        this.employeeMapper = employeeMapper;
        this.auditService = auditService;
    }

    private Double calculateTotalAllocation(Long employeeId) {
        Double sum = allocationRepository.sumAllocationPercentageByEmployeeAndStatusIn(
                employeeId, Arrays.asList(AllocationStatus.ACTIVE));
        return sum != null ? sum : 0.0;
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (request.getUserId() == null) {
            throw new UserNotFoundException("User ID is required to create an employee profile.");
        }
        logger.info("Creating employee for user ID: {}", request.getUserId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0.0);

        Employee saved = employeeRepository.save(employee);
        auditService.logAction("CREATE_EMPLOYEE", "Employee", saved.getEmployeeId(), user.getEmail(), "Created employee profile");
        return employeeMapper.toResponse(saved, 0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return employeeMapper.toResponse(employee, calculateTotalAllocation(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user ID: " + userId));
        return employeeMapper.toResponse(employee, calculateTotalAllocation(employee.getEmployeeId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAllWithUser();
        List<Object[]> groupedAllocations = allocationRepository.sumAllocationPercentageGroupedByEmployee(
                java.util.Arrays.asList(com.erasm.core.enums.AllocationStatus.ACTIVE));
        java.util.Map<Long, Double> allocationMap = groupedAllocations.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));

        return employees.stream()
                .map(emp -> employeeMapper.toResponse(emp, allocationMap.getOrDefault(emp.getEmployeeId(), 0.0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        logger.info("Updating employee profile with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        checkOwnership(employee);

        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        if (request.getExperienceYears() != null) {
            employee.setExperienceYears(request.getExperienceYears());
        }

        Employee updated = employeeRepository.save(employee);
        auditService.logAction("UPDATE_EMPLOYEE", "Employee", updated.getEmployeeId(), updated.getUser().getEmail(), "Updated employee profile");
        return employeeMapper.toResponse(updated, calculateTotalAllocation(id));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee profile with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        employeeRepository.delete(employee);
        auditService.logAction("DELETE_EMPLOYEE", "Employee", id, "ADMIN", "Deleted employee profile ID " + id);
    }

    @Override
    @Transactional
    public EmployeeSkillResponse addOrUpdateSkill(Long employeeId, EmployeeSkillRequest request) {
        logger.info("Adding/Updating skill for employee ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        checkOwnership(employee);

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new SkillNotFoundException("Skill not found with ID: " + request.getSkillId()));

        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployeeEmployeeIdAndSkillSkillId(employeeId, request.getSkillId())
                .orElseGet(() -> {
                    EmployeeSkill es = new EmployeeSkill();
                    es.setEmployee(employee);
                    es.setSkill(skill);
                    return es;
                });

        employeeSkill.setSkillLevel(request.getSkillLevel());
        employeeSkill.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0.0);

        EmployeeSkill saved = employeeSkillRepository.save(employeeSkill);
        auditService.logAction("UPDATE_EMPLOYEE_SKILL", "EmployeeSkill", saved.getEmployeeSkillId(), employee.getUser().getEmail(), "Updated skill: " + skill.getSkillName());

        return new EmployeeSkillResponse(
                saved.getEmployeeSkillId(),
                skill.getSkillId(),
                skill.getSkillName(),
                saved.getSkillLevel(),
                saved.getExperienceYears()
        );
    }

    @Override
    @Transactional
    public CertificationResponse addCertification(Long employeeId, CertificationRequest request) {
        logger.info("Adding certification for employee ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        checkOwnership(employee);

        Certification certification = new Certification();
        certification.setEmployee(employee);
        certification.setCertificationName(request.getCertificationName());
        certification.setIssuingOrganization(request.getIssuingOrganization());
        certification.setIssueDate(request.getIssueDate());
        certification.setExpiryDate(request.getExpiryDate());

        Certification saved = certificationRepository.save(certification);
        auditService.logAction("ADD_CERTIFICATION", "Certification", saved.getCertificationId(), employee.getUser().getEmail(), "Added certification: " + saved.getCertificationName());

        return new CertificationResponse(
                saved.getCertificationId(),
                saved.getCertificationName(),
                saved.getIssuingOrganization(),
                saved.getIssueDate(),
                saved.getExpiryDate()
        );
    }

    @Override
    @Transactional
    public void deleteCertification(Long certificationId) {
        logger.info("Deleting certification with ID: {}", certificationId);
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + certificationId));

        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        String currentEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !certification.getEmployee().getUser().getEmail().equalsIgnoreCase(currentEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to delete this certification.");
        }

        certificationRepository.delete(certification);
        auditService.logAction("DELETE_CERTIFICATION", "Certification", certificationId,
                certification.getEmployee().getUser().getEmail(), "Deleted certification: " + certification.getCertificationName());
    }

    private void checkOwnership(Employee employee) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        String currentEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && employee.getUser() != null && !employee.getUser().getEmail().equalsIgnoreCase(currentEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to modify another employee's profile.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAvailableEmployees(Long skillId, String department, Double minExperience, Double maxAllocation) {
        logger.info("Fetching available employees with skillId={}, department={}, minExperience={}, maxAllocation={}", 
                skillId, department, minExperience, maxAllocation);
        double threshold = (maxAllocation != null) ? maxAllocation : 100.0;

        List<Object[]> groupedAllocations = allocationRepository.sumAllocationPercentageGroupedByEmployee(
                java.util.Arrays.asList(AllocationStatus.ACTIVE));
        java.util.Map<Long, Double> allocationMap = groupedAllocations.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));

        List<Employee> employees;
        if (skillId != null) {
            List<EmployeeSkill> employeeSkills = employeeSkillRepository.findBySkillSkillId(skillId);
            employees = employeeSkills.stream()
                    .map(EmployeeSkill::getEmployee)
                    .filter(e -> e != null && e.getUser() != null)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            employees = employeeRepository.findAllWithUser();
        }

        return employees.stream()
                .filter(emp -> {
                    if (department != null && !department.isBlank()) {
                        if (emp.getDepartment() == null || !emp.getDepartment().equalsIgnoreCase(department.trim())) {
                            return false;
                        }
                    }
                    if (minExperience != null) {
                        if (emp.getExperienceYears() == null || emp.getExperienceYears() < minExperience) {
                            return false;
                        }
                    }
                    double currentAlloc = allocationMap.getOrDefault(emp.getEmployeeId(), 0.0);
                    return currentAlloc <= threshold;
                })
                .map(emp -> employeeMapper.toResponse(emp, allocationMap.getOrDefault(emp.getEmployeeId(), 0.0)))
                .collect(Collectors.toList());
    }
}
