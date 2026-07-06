package com.erasm.core.service;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.request.EmployeeRequest;
import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByUserId(Long userId);
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    EmployeeSkillResponse addOrUpdateSkill(Long employeeId, EmployeeSkillRequest request);
    CertificationResponse addCertification(Long employeeId, CertificationRequest request);
    void deleteCertification(Long certificationId);
    List<EmployeeResponse> getAvailableEmployees(Long skillId, String department, Double minExperience, Double maxAllocation);
}
