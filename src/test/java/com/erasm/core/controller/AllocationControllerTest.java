package com.erasm.core.controller;

import com.erasm.core.dto.request.AllocationRequest;
import com.erasm.core.dto.response.AllocationResponse;
import com.erasm.core.enums.AllocationStatus;
import com.erasm.core.service.AllocationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AllocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AllocationService allocationService;

    @InjectMocks
    private AllocationController allocationController;

    private AllocationResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(allocationController).build();

        response = new AllocationResponse();
        response.setAllocationId(1L);
        response.setEmployeeId(1L);
        response.setEmployeeName("John Doe");
        response.setProjectId(1L);
        response.setProjectName("Healthcare Portal");
        response.setAllocationPercentage(60.0);
        response.setStartDate(LocalDate.now());
        response.setStatus(AllocationStatus.ACTIVE);
    }

    @Test
    void testAllocateEmployee_Success() throws Exception {
        when(allocationService.allocateEmployee(any(AllocationRequest.class))).thenReturn(response);

        String requestJson = "{\"employeeId\":1,\"projectId\":1,\"allocationPercentage\":60.0,\"startDate\":\"2026-06-28\",\"status\":\"ACTIVE\"}";

        mockMvc.perform(post("/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.allocationId").value(1L));

        verify(allocationService).allocateEmployee(any(AllocationRequest.class));
    }

    @Test
    void testReallocateEmployee_Success() throws Exception {
        when(allocationService.reallocateEmployee(eq(1L), eq(80.0))).thenReturn(response);

        String body = "{\"percentage\":80.0}";
        mockMvc.perform(put("/allocations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(allocationService).reallocateEmployee(eq(1L), eq(80.0));
    }

    @Test
    void testReleaseEmployee_Success() throws Exception {
        when(allocationService.releaseEmployee(1L)).thenReturn(response);

        mockMvc.perform(put("/allocations/1/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(allocationService).releaseEmployee(1L);
    }

    @Test
    void testUpdateAllocationStatus_Success() throws Exception {
        when(allocationService.updateAllocationStatus(eq(1L), eq(AllocationStatus.ACTIVE))).thenReturn(response);

        String body = "{\"status\":\"ACTIVE\"}";
        mockMvc.perform(put("/allocations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(allocationService).updateAllocationStatus(eq(1L), eq(AllocationStatus.ACTIVE));
    }

    @Test
    void testGetAllAllocationById_Success() throws Exception {
        when(allocationService.getAllocationById(1L)).thenReturn(response);

        mockMvc.perform(get("/allocations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.allocationId").value(1L));

        verify(allocationService).getAllocationById(1L);
    }

    @Test
    void testGetAllAllocations_Success() throws Exception {
        when(allocationService.getAllAllocations()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));

        verify(allocationService).getAllAllocations();
    }

    @Test
    void testGetAllocationsByEmployee_Success() throws Exception {
        when(allocationService.getAllocationsByEmployee(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/allocations/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));

        verify(allocationService).getAllocationsByEmployee(1L);
    }

    @Test
    void testGetAllocationsByProject_Success() throws Exception {
        when(allocationService.getAllocationsByProject(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/allocations/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].allocationId").value(1L));

        verify(allocationService).getAllocationsByProject(1L);
    }

    @Test
    void testReallocateEmployee_MissingPercentageField_ReturnsBadRequest() throws Exception {
        String body = "{\"otherField\":\"value\"}"; // no percentage key
        mockMvc.perform(put("/allocations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(allocationService, never()).reallocateEmployee(any(), any());
    }

    @Test
    void testReallocateEmployee_NonNumericPercentage_ReturnsBadRequest() throws Exception {
        String body = "{\"percentage\":\"not-a-number\"}";
        mockMvc.perform(put("/allocations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(allocationService, never()).reallocateEmployee(any(), any());
    }

    @Test
    void testUpdateAllocationStatus_NullStatus_ReturnsBadRequest() throws Exception {
        String body = "{\"otherField\":\"value\"}"; // no status key
        mockMvc.perform(put("/allocations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(allocationService, never()).updateAllocationStatus(any(), any());
    }

    @Test
    void testUpdateAllocationStatus_InvalidStatusValue_ReturnsBadRequest() throws Exception {
        String body = "{\"status\":\"TOTALLY_INVALID\"}";
        mockMvc.perform(put("/allocations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(allocationService, never()).updateAllocationStatus(any(), any());
    }

    @Test
    void testReallocateEmployee_WithAllocationPercentageKey() throws Exception {
        // Controller also accepts "allocationPercentage" as key
        when(allocationService.reallocateEmployee(eq(1L), eq(70.0))).thenReturn(response);

        String body = "{\"allocationPercentage\":70.0}";
        mockMvc.perform(put("/allocations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(allocationService).reallocateEmployee(eq(1L), eq(70.0));
    }
}
