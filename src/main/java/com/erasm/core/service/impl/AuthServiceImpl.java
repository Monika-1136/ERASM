package com.erasm.core.service.impl;

import com.erasm.core.dto.request.LoginRequest;
import com.erasm.core.dto.request.RegisterRequest;
import com.erasm.core.dto.request.RefreshTokenRequest;
import com.erasm.core.dto.response.JwtResponse;
import com.erasm.core.dto.response.TokenRefreshResponse;
import com.erasm.core.entity.Employee;
import com.erasm.core.entity.RefreshToken;
import com.erasm.core.entity.Role;
import com.erasm.core.entity.User;
import com.erasm.core.enums.RoleName;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.exception.InvalidRefreshTokenException;
import com.erasm.core.repository.EmployeeRepository;
import com.erasm.core.repository.RoleRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.security.jwt.JwtUtil;
import com.erasm.core.service.AuthService;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService,
                           RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public JwtResponse login(LoginRequest request) {
        logger.info("User login attempt for email: {}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        logger.info("User login successful for email: {}", request.getEmail());
        auditService.logAction("LOGIN_SUCCESS", "User", user.getUserId(), user.getEmail(), "User logged in successfully");

        String roleName = user.getRole() != null ? user.getRole().getRoleName().name() : "";
        return new JwtResponse(jwt, refreshToken.getToken(), user.getUserId(), user.getEmail(), roleName);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getUserId());
        auditService.logAction("REGISTER", "User", savedUser.getUserId(), savedUser.getEmail(), "User registered successfully");

        if (RoleName.ROLE_EMPLOYEE.equals(role.getRoleName())) {
            Employee employee = new Employee();
            employee.setUser(savedUser);
            employee.setDepartment("Engineering");
            employee.setDesignation("Software Engineer");
            employee.setExperienceYears(0.0);
            employeeRepository.save(employee);
            logger.info("Employee profile created for user ID: {}", savedUser.getUserId());
        }
    }

    @Override
    @Transactional
    public void logout(String token) {
        logger.info("User logout requested");
        String username = "UNKNOWN";
        if (token != null && !token.isBlank()) {
            try {
                username = jwtUtil.extractUsername(token);
                userRepository.findByEmail(username).ifPresent(user -> {
                    refreshTokenService.deleteByUser(user.getUserId());
                });
            } catch (Exception e) {
                logger.warn("Could not extract username during logout: " + e.getMessage());
            }
        }
        auditService.logAction("LOGOUT", "User", null, username, "User logged out");
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String requestToken = request.getRefreshToken();
        logger.info("Token refresh request received");

        RefreshToken token = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is not in database."));

        refreshTokenService.verifyExpiration(token);

        User user = token.getUser();
        String accessToken = jwtUtil.generateToken(user.getEmail());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        refreshTokenService.revokeToken(requestToken);
        
        logger.info("Successfully refreshed token for user: {}", user.getEmail());
        return new TokenRefreshResponse(accessToken, newRefreshToken.getToken());
    }

    @Override
    @Transactional
    public void logoutWithRefreshToken(String refreshToken) {
        logger.info("Logging out via refresh token");
        refreshTokenService.findByToken(refreshToken).ifPresent(token -> {
            User user = token.getUser();
            logger.info("Invalidating refresh tokens for user: {}", user.getEmail());
            refreshTokenService.deleteByUser(user.getUserId());
            auditService.logAction("LOGOUT_REFRESH", "User", user.getUserId(), user.getEmail(), "User logged out via refresh token");
        });
    }
}
