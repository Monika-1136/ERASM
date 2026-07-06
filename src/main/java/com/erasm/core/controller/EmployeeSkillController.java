package com.erasm.core.controller;

import com.erasm.core.dto.request.EmployeeSkillRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.EmployeeSkillResponse;
import com.erasm.core.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-skills")
@Tag(name = "Employee Skills", description = "Endpoints for managing employee skill sets and expertise levels")
public class EmployeeSkillController {

    private final EmployeeService employeeService;

    public EmployeeSkillController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @Operation(
        summary = "Add or update employee skill",
        description = "Adds a new skill or updates an existing skill level and experience years for an employee profile.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee skill added/updated successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request details or validation failure")
        }
    )
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> addOrUpdateSkill(@Valid @RequestBody EmployeeSkillRequest request) {
        EmployeeSkillResponse response = employeeService.addOrUpdateSkill(request.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success("Employee skill added/updated successfully", response));
    }
}
