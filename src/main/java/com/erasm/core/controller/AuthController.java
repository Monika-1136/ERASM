package com.erasm.core.controller;

import com.erasm.core.dto.request.LoginRequest;
import com.erasm.core.dto.request.RegisterRequest;
import com.erasm.core.dto.request.RefreshTokenRequest;
import com.erasm.core.dto.request.LogoutRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.JwtResponse;
import com.erasm.core.dto.response.TokenRefreshResponse;
import com.erasm.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Registers a new user profile with either a role ID or a role name.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request details or validation failure")
        }
    )
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", "Registration completed"));
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticates user credentials and returns a JWT access token along with a refresh token.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid email or password")
        }
    )
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Obtains a new JWT access token and a new refresh token using a valid refresh token.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refresh token or token expired")
        }
    )
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "User logout",
        description = "Logs out the authenticated user and invalidates the provided refresh token.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful", 
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        }
    )
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletRequest request,
            @Valid @RequestBody(required = false) LogoutRequest logoutRequest) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        if (logoutRequest != null && logoutRequest.getRefreshToken() != null && !logoutRequest.getRefreshToken().isBlank()) {
            authService.logoutWithRefreshToken(logoutRequest.getRefreshToken());
        }
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
