package com.erasm.core.service.impl;

import com.erasm.core.dto.request.ChangePasswordRequest;
import com.erasm.core.dto.request.UserRequest;
import com.erasm.core.dto.response.UserResponse;
import com.erasm.core.entity.Role;
import com.erasm.core.entity.User;
import com.erasm.core.enums.RoleName;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.exception.UserNotFoundException;
import com.erasm.core.mapper.UserMapper;
import com.erasm.core.repository.RoleRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.service.AuditService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRoleId(1L);
        role.setRoleName(RoleName.ROLE_EMPLOYEE);

        user = new User();
        user.setUserId(10L);
        user.setEmail("test@erasm.com");
        user.setFullName("Test User");
        user.setPassword("encodedPassword");
        user.setRole(role);

        userRequest = new UserRequest();
        userRequest.setEmail("test@erasm.com");
        userRequest.setFullName("Test User");
        userRequest.setPassword("password123");
        userRequest.setRoleId(1L);
    }

    @AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    private void mockUserAuthentication(String email, boolean isAdmin) {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        
        List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities;
        if (isAdmin) {
            authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        }
        doReturn(authorities).when(auth).getAuthorities();

        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setUserId(10L);
        mockResponse.setEmail("test@erasm.com");
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals(10L, response.getUserId());
        verify(userRepository).save(user);
        verify(auditService).logAction(eq("CREATE_USER"), eq("User"), eq(10L), eq("ADMIN"), anyString());
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void testCreateUser_RoleNotFound() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setUserId(10L);
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        UserResponse response = userService.getUserById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getUserId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(10L));
    }

    @Test
    void testGetAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setUserId(10L);
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        List<UserResponse> list = userService.getAllUsers();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setUserId(10L);
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        UserResponse response = userService.updateUser(10L, userRequest);

        assertNotNull(response);
        verify(userRepository).save(user);
        verify(auditService).logAction(eq("UPDATE_USER"), eq("User"), eq(10L), eq("ADMIN"), anyString());
    }

    @Test
    void testUpdateUser_DuplicateEmail() {
        userRequest.setEmail("another@erasm.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("another@erasm.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(10L, userRequest));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        userService.deleteUser(10L);

        verify(userRepository).delete(user);
        verify(auditService).logAction(eq("DELETE_USER"), eq("User"), eq(10L), eq("ADMIN"), anyString());
    }

    @Test
    void testChangePassword_Success() {
        mockUserAuthentication("test@erasm.com", false);
        ChangePasswordRequest cpRequest = new ChangePasswordRequest();
        cpRequest.setOldPassword("password123");
        cpRequest.setNewPassword("newPassword456");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("newEncodedPassword");

        userService.changePassword(10L, cpRequest);

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
        verify(auditService).logAction(eq("CHANGE_PASSWORD"), eq("User"), eq(10L), eq("test@erasm.com"), anyString());
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        mockUserAuthentication("test@erasm.com", false);
        ChangePasswordRequest cpRequest = new ChangePasswordRequest();
        cpRequest.setOldPassword("wrongPassword");
        cpRequest.setNewPassword("newPassword456");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(10L, cpRequest));
    }
}
