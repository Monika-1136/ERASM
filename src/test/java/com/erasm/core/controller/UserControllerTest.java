package com.erasm.core.controller;

import com.erasm.core.dto.request.ChangePasswordRequest;
import com.erasm.core.dto.request.UserRequest;
import com.erasm.core.dto.response.UserResponse;
import com.erasm.core.exception.GlobalExceptionHandler;
import com.erasm.core.exception.UserNotFoundException;
import com.erasm.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link UserController}.
 * Covers all user management endpoints with success and error paths.
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userResponse = new UserResponse();
        userResponse.setUserId(1L);
        userResponse.setFullName("John Admin");
        userResponse.setEmail("admin@erasm.com");
        userResponse.setRole("ADMIN");
    }

    // ========================= POST /api/users =========================

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);
        String json = "{\"fullName\":\"John Admin\",\"email\":\"admin@erasm.com\",\"password\":\"Admin@123\",\"roleId\":1}";
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.email").value("admin@erasm.com"));
        verify(userService).createUser(any(UserRequest.class));
    }

    // ========================= GET /api/users/{id} =========================

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.fullName").value("John Admin"));
        verify(userService).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("User not found with ID: 99"));
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
        verify(userService).getUserById(99L);
    }

    // ========================= GET /api/users =========================

    @Test
    void testGetAllUsers_Success() throws Exception {
        UserResponse user2 = new UserResponse();
        user2.setUserId(2L);
        user2.setFullName("Manager Jane");
        user2.setEmail("manager@erasm.com");
        user2.setRole("RESOURCE_MANAGER");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(userResponse, user2));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].email").value("admin@erasm.com"))
                .andExpect(jsonPath("$.data[1].role").value("RESOURCE_MANAGER"));
        verify(userService).getAllUsers();
    }

    @Test
    void testGetAllUsers_Empty() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ========================= PUT /api/users/{id} =========================

    @Test
    void testUpdateUser_Success() throws Exception {
        UserResponse updated = new UserResponse();
        updated.setUserId(1L);
        updated.setFullName("John Updated");
        updated.setEmail("admin@erasm.com");
        updated.setRole("ADMIN");
        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(updated);
        String json = "{\"fullName\":\"John Updated\",\"email\":\"admin@erasm.com\",\"password\":\"NewPass@1\",\"roleId\":1}";
        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Updated"));
        verify(userService).updateUser(eq(1L), any(UserRequest.class));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        when(userService.updateUser(eq(99L), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found with ID: 99"));
        String json = "{\"fullName\":\"Nobody\",\"email\":\"nobody@erasm.com\",\"password\":\"Pass@1234\",\"roleId\":1}";
        mockMvc.perform(put("/api/users/99").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    // ========================= DELETE /api/users/{id} =========================

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
        verify(userService).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found with ID: 99")).when(userService).deleteUser(99L);
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // ========================= PUT /api/users/{id}/change-password =========================

    @Test
    void testChangePassword_Success() throws Exception {
        doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));
        String json = "{\"oldPassword\":\"OldPass@1\",\"newPassword\":\"NewPass@1\"}";
        mockMvc.perform(put("/api/users/1/change-password").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
        verify(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));
    }

    @Test
    void testChangePassword_UserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found with ID: 99"))
                .when(userService).changePassword(eq(99L), any(ChangePasswordRequest.class));
        String json = "{\"oldPassword\":\"OldPass@1\",\"newPassword\":\"NewPass@1\"}";
        mockMvc.perform(put("/api/users/99/change-password").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }
}
