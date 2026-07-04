package com.erasm.core.controller;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.request.EmployeeRequest;
import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.dto.response.EmployeeResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/employees", "/api/employees"})
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee profile created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", employeeService.getAllEmployees()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'EMPLOYEE', 'AUDITOR')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee profile fetched successfully", employeeService.getEmployeeById(id)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'EMPLOYEE', 'AUDITOR')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Employee profile fetched successfully", employeeService.getEmployeeByUserId(userId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee profile updated successfully", employeeService.updateEmployee(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee profile deleted successfully"));
    }

    @PostMapping("/{id}/skills")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> addOrUpdateSkill(@PathVariable Long id, @Valid @RequestBody EmployeeSkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee skill added/updated successfully", employeeService.addOrUpdateSkill(id, request)));
    }

    @PostMapping("/{id}/certifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CertificationResponse>> addCertification(@PathVariable Long id, @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certification added successfully", employeeService.addCertification(id, request)));
    }
}
