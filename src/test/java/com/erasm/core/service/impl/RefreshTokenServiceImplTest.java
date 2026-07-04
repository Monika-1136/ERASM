package com.erasm.core.service.impl;

import com.erasm.core.entity.RefreshToken;
import com.erasm.core.entity.User;
import com.erasm.core.exception.InvalidRefreshTokenException;
import com.erasm.core.exception.RefreshTokenExpiredException;
import com.erasm.core.repository.RefreshTokenRepository;
import com.erasm.core.repository.UserRepository;
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
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("user@erasm.com");

        refreshToken = new RefreshToken();
        refreshToken.setId(100L);
        refreshToken.setUser(user);
        refreshToken.setToken("valid-token");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(600));
        refreshToken.setRevoked(false);
    }

    @Test
    void testCreateRefreshToken_Success() {
        when(userRepository.findByEmail("user@erasm.com")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken("user@erasm.com");

        assertNotNull(result);
        assertEquals("valid-token", result.getToken());
        verify(userRepository).findByEmail("user@erasm.com");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testCreateRefreshToken_UserNotFound() {
        when(userRepository.findByEmail("unknown@erasm.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> refreshTokenService.createRefreshToken("unknown@erasm.com"));
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void testFindByToken_Success() {
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken("valid-token");

        assertTrue(result.isPresent());
        assertEquals("valid-token", result.get().getToken());
    }

    @Test
    void testVerifyExpiration_NotExpired() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        assertEquals(refreshToken, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void testVerifyExpiration_Expired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(10));

        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void testVerifyExpiration_Revoked() {
        refreshToken.setRevoked(true);

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void testDeleteByUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        refreshTokenService.deleteByUser(1L);

        verify(userRepository).findById(1L);
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void testDeleteByUser_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        refreshTokenService.deleteByUser(2L);

        verify(refreshTokenRepository, never()).deleteByUser(any());
    }

    @Test
    void testRevokeToken_Success() {
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        refreshTokenService.revokeToken("valid-token");

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository).findByToken("valid-token");
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void testRevokeToken_NotFound() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        refreshTokenService.revokeToken("invalid-token");

        verify(refreshTokenRepository).findByToken("invalid-token");
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void testCleanExpiredTokens() {
        doNothing().when(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));

        refreshTokenService.cleanExpiredTokens();

        verify(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));
    }
}
