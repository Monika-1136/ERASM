package com.erasm.core.controller;

import com.erasm.core.dto.request.CertificationRequest;
import com.erasm.core.dto.response.CertificationResponse;
import com.erasm.core.entity.Certification;
import com.erasm.core.exception.GlobalExceptionHandler;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.repository.CertificationRepository;
import com.erasm.core.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CertificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private CertificationController certificationController;

    private Certification certification;
    private CertificationResponse certResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        certification = new Certification();
        certification.setCertificationId(1L);
        certification.setCertificationName("AWS Solutions Architect");
        certification.setIssuingOrganization("Amazon");
        certification.setIssueDate(LocalDate.now());
        certification.setExpiryDate(LocalDate.now().plusYears(3));

        certResponse = new CertificationResponse(1L, "AWS Solutions Architect", "Amazon",
                LocalDate.of(2024, 1, 1), LocalDate.of(2027, 1, 1));
    }

    @Test
    void testAddCertification_Success() throws Exception {
        when(employeeService.addCertification(eq(1L), any(CertificationRequest.class))).thenReturn(certResponse);
        String json = "{\"employeeId\":1,\"certificationName\":\"AWS Solutions Architect\",\"issuingOrganization\":\"Amazon\",\"issueDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/api/certifications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.certificationName").value("AWS Solutions Architect"));
        verify(employeeService).addCertification(eq(1L), any(CertificationRequest.class));
    }

    @Test
    void testAddCertification_EmployeeNotFound() throws Exception {
        when(employeeService.addCertification(eq(99L), any(CertificationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 99"));
        String json = "{\"employeeId\":99,\"certificationName\":\"AWS Solutions Architect\",\"issuingOrganization\":\"Amazon\",\"issueDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/api/certifications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCertificationsByEmployee_Success() throws Exception {
        when(certificationRepository.findByEmployeeEmployeeId(1L)).thenReturn(Collections.singletonList(certification));

        mockMvc.perform(get("/api/certifications/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].certificationId").value(1L))
                .andExpect(jsonPath("$.data[0].certificationName").value("AWS Solutions Architect"));

        verify(certificationRepository).findByEmployeeEmployeeId(1L);
    }

    @Test
    void testDeleteCertification_Success() throws Exception {
        doNothing().when(employeeService).deleteCertification(1L);

        mockMvc.perform(delete("/api/certifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(employeeService).deleteCertification(1L);
    }

    @Test
    void testDeleteCertification_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Certification not found with ID: 1")).when(employeeService).deleteCertification(1L);

        mockMvc.perform(delete("/api/certifications/1"))
                .andExpect(status().isNotFound());

        verify(employeeService).deleteCertification(1L);
    }
}
