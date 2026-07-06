package com.erasm.core.controller;

import com.erasm.core.dto.request.ResourceRequestDto;
import com.erasm.core.dto.response.ResourceRequestResponse;
import com.erasm.core.enums.RequestStatus;
import com.erasm.core.enums.SkillLevel;
import com.erasm.core.service.ResourceRequestService;
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
public class ResourceRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceRequestService resourceRequestService;

    @InjectMocks
    private ResourceRequestController resourceRequestController;

    private ResourceRequestResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceRequestController).build();

        response = new ResourceRequestResponse();
        response.setRequestId(1L);
        response.setProjectId(1L);
        response.setProjectName("Healthcare Portal");
        response.setSkillId(1L);
        response.setSkillName("Java");
        response.setRequiredCount(3);
        response.setRequiredLevel(SkillLevel.ADVANCED);
        response.setStatus(RequestStatus.SUBMITTED);
        response.setRequestedBy("Delivery Manager");
        response.setCreatedDate(LocalDate.now());
    }

    @Test
    void testCreateResourceRequest_Success() throws Exception {
        when(resourceRequestService.createResourceRequest(any(ResourceRequestDto.class))).thenReturn(response);

        String requestJson = "{\"projectId\":1,\"skillId\":1,\"requiredCount\":3,\"requiredLevel\":\"ADVANCED\",\"status\":\"SUBMITTED\"}";

        mockMvc.perform(post("/api/resource-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1L));

        verify(resourceRequestService).createResourceRequest(any(ResourceRequestDto.class));
    }

    @Test
    void testGetRequestById_Success() throws Exception {
        when(resourceRequestService.getRequestById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/resource-requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1L));

        verify(resourceRequestService).getRequestById(1L);
    }

    @Test
    void testGetAllRequests_Success() throws Exception {
        when(resourceRequestService.getAllRequests()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/resource-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].requestId").value(1L));

        verify(resourceRequestService).getAllRequests();
    }

    @Test
    void testGetRequestsByProject_Success() throws Exception {
        when(resourceRequestService.getRequestsByProject(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/resource-requests/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].requestId").value(1L));

        verify(resourceRequestService).getRequestsByProject(1L);
    }

    @Test
    void testUpdateRequestStatus_Success() throws Exception {
        when(resourceRequestService.updateRequestStatus(eq(1L), eq(RequestStatus.APPROVED))).thenReturn(response);

        String body = "{\"status\":\"APPROVED\"}";
        mockMvc.perform(put("/api/resource-requests/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1L));

        verify(resourceRequestService).updateRequestStatus(eq(1L), eq(RequestStatus.APPROVED));
    }

    @Test
    void testUpdateRequestStatus_NullStatus_ReturnsBadRequest() throws Exception {
        String body = "{\"otherField\":\"value\"}"; // no "status" key
        mockMvc.perform(put("/api/resource-requests/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(resourceRequestService, never()).updateRequestStatus(any(), any());
    }

    @Test
    void testUpdateRequestStatus_BlankStatus_ReturnsBadRequest() throws Exception {
        String body = "{\"status\":\"   \"}"; // blank
        mockMvc.perform(put("/api/resource-requests/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(resourceRequestService, never()).updateRequestStatus(any(), any());
    }

    @Test
    void testUpdateRequestStatus_InvalidStatusValue_ReturnsBadRequest() throws Exception {
        String body = "{\"status\":\"INVALID_STATUS\"}";
        mockMvc.perform(put("/api/resource-requests/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(resourceRequestService, never()).updateRequestStatus(any(), any());
    }
}
