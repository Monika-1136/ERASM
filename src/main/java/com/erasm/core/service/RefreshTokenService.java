package com.erasm.core.service;

import com.erasm.core.entity.RefreshToken;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void revokeToken(String token);

    void deleteByUser(Long userId);

    void cleanExpiredTokens();
}
