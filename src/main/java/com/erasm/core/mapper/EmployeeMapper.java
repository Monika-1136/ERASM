package com.erasm.core.mapper;

import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.entity.Employee;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee, Double totalAllocationPercentage) {
        if (employee == null) return null;
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        if (employee.getUser() != null) {
            response.setUserId(employee.getUser().getUserId());
            response.setFullName(employee.getUser().getFullName());
            response.setEmail(employee.getUser().getEmail());
        }
        response.setDepartment(employee.getDepartment());
        response.setDesignation(employee.getDesignation());
        response.setExperienceYears(employee.getExperienceYears());
        response.setTotalAllocationPercentage(totalAllocationPercentage != null ? totalAllocationPercentage : 0.0);
        response.setAvailablePercentage(100.0 - response.getTotalAllocationPercentage());
        response.setAvailabilityStatus(response.getTotalAllocationPercentage() == 0.0 ? "AVAILABLE" : 
                                      (response.getTotalAllocationPercentage() >= 100.0 ? "FULLY_ALLOCATED" : "PARTIALLY_ALLOCATED"));

        if (employee.getAllocations() != null) {
            response.setProjects(employee.getAllocations().stream()
                    .filter(a -> a.getStatus() == com.erasm.core.enums.AllocationStatus.ACTIVE)
                    .map(a -> a.getProject() != null ? a.getProject().getProjectName() : "Unknown Project")
                    .distinct()
                    .collect(Collectors.toList()));
        }

        if (employee.getEmployeeSkills() != null) {
            response.setSkills(employee.getEmployeeSkills().stream().map(es -> new EmployeeSkillResponse(
                    es.getEmployeeSkillId(),
                    es.getSkill() != null ? es.getSkill().getSkillId() : null,
                    es.getSkill() != null ? es.getSkill().getSkillName() : null,
                    es.getSkillLevel(),
                    es.getExperienceYears()
            )).collect(Collectors.toList()));
        }

        if (employee.getCertifications() != null) {
            response.setCertifications(employee.getCertifications().stream().map(c -> new CertificationResponse(
                    c.getCertificationId(),
                    c.getCertificationName(),
                    c.getIssuingOrganization(),
                    c.getIssueDate(),
                    c.getExpiryDate()
            )).collect(Collectors.toList()));
        }

        return response;
    }
}
