package com.erasm.core.controller;

import com.erasm.core.dto.request.RoleRequest;
import com.erasm.core.dto.response.RoleResponse;
import com.erasm.core.enums.RoleName;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.GlobalExceptionHandler;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        roleResponse = new RoleResponse(1L, RoleName.ROLE_RESOURCE_MANAGER);
    }

    @Test
    void testCreateRole_Success() throws Exception {
        when(roleService.createRole(any(RoleRequest.class))).thenReturn(roleResponse);
        String json = "{\"roleName\":\"ROLE_RESOURCE_MANAGER\"}";
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roleId").value(1L))
                .andExpect(jsonPath("$.data.roleName").value("ROLE_RESOURCE_MANAGER"));
        verify(roleService).createRole(any(RoleRequest.class));
    }

    @Test
    void testCreateRole_Duplicate() throws Exception {
        when(roleService.createRole(any(RoleRequest.class)))
                .thenThrow(new DuplicateResourceException("Role already exists"));
        String json = "{\"roleName\":\"ROLE_RESOURCE_MANAGER\"}";
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllRoles_Success() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Collections.singletonList(roleResponse));
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].roleName").value("ROLE_RESOURCE_MANAGER"));
        verify(roleService).getAllRoles();
    }

    @Test
    void testGetRoleById_Success() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(roleResponse);
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleId").value(1L));
        verify(roleService).getRoleById(1L);
    }

    @Test
    void testGetRoleById_NotFound() throws Exception {
        when(roleService.getRoleById(99L)).thenThrow(new ResourceNotFoundException("Role not found"));
        mockMvc.perform(get("/api/roles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        when(roleService.updateRole(eq(1L), any(RoleRequest.class))).thenReturn(roleResponse);
        String json = "{\"roleName\":\"ROLE_RESOURCE_MANAGER\"}";
        mockMvc.perform(put("/api/roles/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(roleService).updateRole(eq(1L), any(RoleRequest.class));
    }

    @Test
    void testDeleteRole_Success() throws Exception {
        doNothing().when(roleService).deleteRole(1L);
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(roleService).deleteRole(1L);
    }

    @Test
    void testDeleteRole_AssignedToUsers() throws Exception {
        doThrow(new IllegalArgumentException("Cannot delete role")).when(roleService).deleteRole(1L);
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isBadRequest());
    }
}
