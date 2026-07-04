package com.erasm.core.service.impl;

import com.erasm.core.entity.RefreshToken;
import com.erasm.core.entity.User;
import com.erasm.core.exception.InvalidRefreshTokenException;
import com.erasm.core.exception.RefreshTokenExpiredException;
import com.erasm.core.exception.UserNotFoundException;
import com.erasm.core.repository.RefreshTokenRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 7L * 24 * 60 * 60;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        logger.info("Generating refresh token for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            logger.warn("Refresh token is expired for user ID: {}", token.getUser().getUserId());
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token was expired. Please make a new signin request.");
        }
        if (token.isRevoked()) {
            logger.warn("Refresh token is revoked for user ID: {}", token.getUser().getUserId());
            throw new InvalidRefreshTokenException("Refresh token is revoked.");
        }
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            logger.info("Revoking refresh token for user ID: {}", refreshToken.getUser().getUserId());
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void deleteByUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            logger.info("Deleting all refresh tokens for user ID: {}", userId);
            refreshTokenRepository.deleteByUser(user);
        });
    }

    @Override
    @Transactional
    public void cleanExpiredTokens() {
        logger.info("Purging expired refresh tokens from database");
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
