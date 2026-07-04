package com.erasm.core.service.impl;

import com.erasm.core.dto.request.LoginRequest;
import com.erasm.core.dto.request.RegisterRequest;
import com.erasm.core.dto.response.JwtResponse;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.Role;
import com.erasm.core.entity.User;
import com.erasm.core.enums.RoleName;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.RoleRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.security.jwt.JwtUtil;
import com.erasm.core.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.erasm.core.service.AuditService auditService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role employeeRole;
    private Role adminRole;
    private User employeeUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        employeeRole = new Role();
        employeeRole.setRoleId(1L);
        employeeRole.setRoleName(RoleName.ROLE_EMPLOYEE);

        adminRole = new Role();
        adminRole.setRoleId(2L);
        adminRole.setRoleName(RoleName.ROLE_ADMIN);

        employeeUser = new User();
        employeeUser.setUserId(10L);
        employeeUser.setEmail("test@erasm.com");
        employeeUser.setFullName("Test User");
        employeeUser.setRole(employeeRole);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@erasm.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setRoleId(1L);
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@erasm.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@erasm.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken("test@erasm.com")).thenReturn("dummy-jwt-token");
        when(userRepository.findByEmail("test@erasm.com")).thenReturn(Optional.of(employeeUser));

        com.erasm.core.entity.RefreshToken mockRefreshToken = new com.erasm.core.entity.RefreshToken();
        mockRefreshToken.setToken("dummy-refresh-token");
        when(refreshTokenService.createRefreshToken("test@erasm.com")).thenReturn(mockRefreshToken);

        JwtResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("dummy-jwt-token", response.getToken());
        assertEquals("dummy-refresh-token", response.getRefreshToken());
        assertEquals(10L, response.getUserId());
        assertEquals("test@erasm.com", response.getEmail());
        assertEquals("ROLE_EMPLOYEE", response.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("test@erasm.com");
        verify(userRepository).findByEmail("test@erasm.com");
        verify(refreshTokenService).createRefreshToken("test@erasm.com");
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@erasm.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@erasm.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken("test@erasm.com")).thenReturn("dummy-jwt-token");
        when(userRepository.findByEmail("test@erasm.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testRegister_Success_Employee() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(employeeRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(employeeUser);

        authService.register(registerRequest);

        verify(userRepository).existsByEmail("test@erasm.com");
        verify(roleRepository).findById(1L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testRegister_Success_NonEmployee() {
        registerRequest.setRoleId(2L);
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        User adminUser = new User();
        adminUser.setUserId(11L);
        adminUser.setEmail("test@erasm.com");
        adminUser.setFullName("Test User");
        adminUser.setRole(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        authService.register(registerRequest);

        verify(userRepository).existsByEmail("test@erasm.com");
        verify(roleRepository).findById(2L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));

        verify(userRepository).existsByEmail("test@erasm.com");
        verify(roleRepository, never()).findById(anyLong());
    }

    @Test
    void testRegister_RoleNotFound() {
        when(userRepository.existsByEmail("test@erasm.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(registerRequest));

        verify(userRepository).existsByEmail("test@erasm.com");
        verify(roleRepository).findById(1L);
    }

    @Test
    void testLogout_WithValidToken() {
        when(jwtUtil.extractUsername("valid-token")).thenReturn("test@erasm.com");
        assertDoesNotThrow(() -> authService.logout("valid-token"));
        verify(jwtUtil).extractUsername("valid-token");
        verify(auditService).logAction(eq("LOGOUT"), eq("User"), isNull(), eq("test@erasm.com"), anyString());
    }

    @Test
    void testLogout_WithNullToken() {
        assertDoesNotThrow(() -> authService.logout(null));
        verify(jwtUtil, never()).extractUsername(any());
        verify(auditService).logAction(eq("LOGOUT"), eq("User"), isNull(), eq("UNKNOWN"), anyString());
    }

    @Test
    void testLogout_WithBlankToken() {
        assertDoesNotThrow(() -> authService.logout("  "));
        verify(jwtUtil, never()).extractUsername(any());
    }

    @Test
    void testLogout_WithInvalidToken_ExtractionFails() {
        when(jwtUtil.extractUsername("bad-token")).thenThrow(new RuntimeException("Invalid JWT"));
        assertDoesNotThrow(() -> authService.logout("bad-token"));
        verify(jwtUtil).extractUsername("bad-token");
        // Should log UNKNOWN user
        verify(auditService).logAction(eq("LOGOUT"), eq("User"), isNull(), eq("UNKNOWN"), anyString());
    }

    @Test
    void testLogin_UserWithNullRole() {
        employeeUser.setRole(null);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@erasm.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@erasm.com");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("test@erasm.com")).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@erasm.com")).thenReturn(Optional.of(employeeUser));

        com.erasm.core.entity.RefreshToken mockRefreshToken = new com.erasm.core.entity.RefreshToken();
        mockRefreshToken.setToken("dummy-refresh-token");
        when(refreshTokenService.createRefreshToken("test@erasm.com")).thenReturn(mockRefreshToken);

        JwtResponse response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("", response.getRole()); // null role → empty string
    }
}
