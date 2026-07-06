package com.erasm.core.controller;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.entity.Certification;
import com.erasm.core.repository.CertificationRepository;
import com.erasm.core.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationRepository certificationRepository;
    private final EmployeeService employeeService;

    public CertificationController(CertificationRepository certificationRepository, EmployeeService employeeService) {
        this.certificationRepository = certificationRepository;
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CertificationResponse>> addCertification(@Valid @RequestBody CertificationRequest request) {
        CertificationResponse response = employeeService.addCertification(request.getEmployeeId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certification added successfully", response));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'RESOURCE_MANAGER', 'DELIVERY_MANAGER', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getCertificationsByEmployee(@PathVariable Long employeeId) {
        List<Certification> certifications = certificationRepository.findByEmployeeEmployeeId(employeeId);
        List<CertificationResponse> responses = certifications.stream()
                .map(cert -> new CertificationResponse(
                        cert.getCertificationId(),
                        cert.getCertificationName(),
                        cert.getIssuingOrganization(),
                        cert.getIssueDate(),
                        cert.getExpiryDate()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Certifications retrieved successfully", responses));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(@PathVariable Long id) {
        employeeService.deleteCertification(id);
        return ResponseEntity.ok(ApiResponse.success("Certification deleted successfully"));
    }
}
