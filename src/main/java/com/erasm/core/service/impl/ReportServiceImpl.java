package com.erasm.core.service.impl;

import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.dto.response.SkillReportResponse;
import com.erasm.core.dto.response.UtilizationReportResponse;
import com.erasm.core.dto.response.DashboardResponse;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.EmployeeSkill;
import com.erasm.core.entity.Skill;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.mapper.AllocationMapper;
import com.erasm.core.repository.AllocationRepository;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.EmployeeSkillRepository;
import com.erasm.core.repository.SkillRepository;
import com.erasm.core.repository.ProjectRepository;
import com.erasm.core.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final AllocationRepository allocationRepository;
    private final AllocationMapper allocationMapper;
    private final ProjectRepository projectRepository;

    public ReportServiceImpl(EmployeeRepository employeeRepository,
                             SkillRepository skillRepository,
                             EmployeeSkillRepository employeeSkillRepository,
                             AllocationRepository allocationRepository,
                             AllocationMapper allocationMapper,
                             ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.employeeSkillRepository = employeeSkillRepository;
        this.allocationRepository = allocationRepository;
        this.allocationMapper = allocationMapper;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillReportResponse> getSkillReport() {
        logger.info("Generating Skill Report");
        List<Skill> skills = skillRepository.findAll();
        List<EmployeeSkill> allEmployeeSkills = employeeSkillRepository.findAllWithSkillAndEmployee();

        java.util.Map<Long, List<EmployeeSkill>> esGrouped = allEmployeeSkills.stream()
                .filter(es -> es.getSkill() != null)
                .collect(Collectors.groupingBy(es -> es.getSkill().getSkillId()));

        List<SkillReportResponse> report = new ArrayList<>();

        for (Skill skill : skills) {
            List<EmployeeSkill> employeeSkills = esGrouped.getOrDefault(skill.getSkillId(), new ArrayList<>());
            List<EmployeeSkillResponse> esResponses = employeeSkills.stream().map(es -> new EmployeeSkillResponse(
                    es.getEmployeeSkillId(),
                    skill.getSkillId(),
                    skill.getSkillName(),
                    es.getSkillLevel(),
                    es.getExperienceYears()
            )).collect(Collectors.toList());

            report.add(new SkillReportResponse(
                    skill.getSkillId(),
                    skill.getSkillName(),
                    skill.getCategory(),
                    esResponses.size(),
                    esResponses
            ));
        }
        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilizationReportResponse> getUtilizationReport() {
        logger.info("Generating Utilization Report");
        List<Employee> employees = employeeRepository.findAllWithUser();
        List<Object[]> groupedAllocations = allocationRepository.sumAllocationPercentageGroupedByEmployee(
                Arrays.asList(AllocationStatus.ACTIVE));
        java.util.Map<Long, Double> allocationMap = groupedAllocations.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));

        List<UtilizationReportResponse> report = new ArrayList<>();

        for (Employee emp : employees) {
            Double allocated = allocationMap.getOrDefault(emp.getEmployeeId(), 0.0);
            if (allocated > 100.0) allocated = 100.0;

            Double billable = allocated;
            Double bench = 100.0 - billable;

            String name = emp.getUser() != null ? emp.getUser().getFullName() : "Employee #" + emp.getEmployeeId();

            report.add(new UtilizationReportResponse(
                    emp.getEmployeeId(),
                    name,
                    emp.getDepartment(),
                    allocated,
                    billable,
                    bench
            ));
        }
        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getProjectAllocationReport() {
        logger.info("Generating Project Allocation Report");
        return allocationRepository.findAllWithEmployeeAndProject().stream()
                .map(allocationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardMetrics() {
        logger.info("Calculating Dashboard Metrics");
        List<Employee> employees = employeeRepository.findAll();
        long totalEmployees = employees.size();
        long totalProjects = projectRepository.count();

        List<Object[]> groupedAllocations = allocationRepository.sumAllocationPercentageGroupedByEmployee(
                Arrays.asList(AllocationStatus.ACTIVE));
        java.util.Map<Long, Double> allocationMap = groupedAllocations.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));

        long benchEmployeesCount = 0;
        long overallocatedEmployeesCount = 0;
        double totalAllocationPercentageSum = 0.0;

        for (Employee emp : employees) {
            double currentAlloc = allocationMap.getOrDefault(emp.getEmployeeId(), 0.0);
            totalAllocationPercentageSum += currentAlloc;
            if (currentAlloc == 0.0) {
                benchEmployeesCount++;
            } else if (currentAlloc > 100.0) {
                overallocatedEmployeesCount++;
            }
        }

        double avgAllocation = totalEmployees > 0 ? (totalAllocationPercentageSum / totalEmployees) : 0.0;
        double billablePercentage = Math.min(avgAllocation, 100.0);
        double benchPercentage = 100.0 - billablePercentage;
        double utilizationPercentage = billablePercentage;
        double availablePercentage = benchPercentage;

        return new DashboardResponse(
                billablePercentage,
                benchPercentage,
                utilizationPercentage,
                availablePercentage,
                avgAllocation,
                totalProjects,
                benchEmployeesCount,
                overallocatedEmployeesCount
        );
    }
}
