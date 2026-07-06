package com.erasm.core.service.impl;

import com.erasm.core.dto.request.RefreshTokenRequest;
import com.erasm.core.dto.response.TokenRefreshResponse;
import com.erasm.core.entity.RefreshToken;
import com.erasm.core.entity.Role;
import com.erasm.core.entity.User;
import com.erasm.core.enums.RoleName;
import com.erasm.core.exception.InvalidRefreshTokenException;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.security.jwt.JwtUtil;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplRefreshTokenTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    @SuppressWarnings("unused")
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(RoleName.ROLE_EMPLOYEE);

        user = new User();
        user.setUserId(1L);
        user.setEmail("user@erasm.com");
        user.setRole(role);

        refreshToken = new RefreshToken();
        refreshToken.setId(100L);
        refreshToken.setUser(user);
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(600));
    }

    @Test
    void testRefreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        
        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.generateToken("user@erasm.com")).thenReturn("new-access-token");
        
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new-refresh-token");
        when(refreshTokenService.createRefreshToken("user@erasm.com")).thenReturn(newRefreshToken);
        doNothing().when(refreshTokenService).revokeToken("valid-refresh-token");

        TokenRefreshResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        
        verify(refreshTokenService).findByToken("valid-refresh-token");
        verify(refreshTokenService).verifyExpiration(refreshToken);
        verify(jwtUtil).generateToken("user@erasm.com");
        verify(refreshTokenService).createRefreshToken("user@erasm.com");
        verify(refreshTokenService).revokeToken("valid-refresh-token");
    }

    @Test
    void testRefreshToken_TokenNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");
        when(refreshTokenService.findByToken("invalid-refresh-token")).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request));
        verify(refreshTokenService, never()).verifyExpiration(any());
    }

    @Test
    void testLogoutWithRefreshToken_Success() {
        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        doNothing().when(refreshTokenService).deleteByUser(1L);

        authService.logoutWithRefreshToken("valid-refresh-token");

        verify(refreshTokenService).findByToken("valid-refresh-token");
        verify(refreshTokenService).deleteByUser(1L);
        verify(auditService).logAction(eq("LOGOUT_REFRESH"), eq("User"), eq(1L), eq("user@erasm.com"), anyString());
    }
}
