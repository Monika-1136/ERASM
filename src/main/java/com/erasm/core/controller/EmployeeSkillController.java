package com.erasm.core.controller;

import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-skills")
public class EmployeeSkillController {

    private final EmployeeService employeeService;

    public EmployeeSkillController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> addOrUpdateSkill(@Valid @RequestBody EmployeeSkillRequest request) {
        EmployeeSkillResponse response = employeeService.addOrUpdateSkill(request.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success("Employee skill added/updated successfully", response));
    }
}
