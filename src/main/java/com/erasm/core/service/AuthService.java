package com.erasm.core.service;

import com.erasm.core.dto.request.LoginRequest;
import com.erasm.core.dto.request.RegisterRequest;
import com.erasm.core.dto.response.JwtResponse;

import com.erasm.core.dto.request.RefreshTokenRequest;
import com.erasm.core.dto.response.TokenRefreshResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    void register(RegisterRequest request);
    void logout(String token);
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);
    void logoutWithRefreshToken(String refreshToken);
}
