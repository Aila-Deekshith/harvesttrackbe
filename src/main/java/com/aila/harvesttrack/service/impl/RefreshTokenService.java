package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.model.RefreshToken;
import com.aila.harvesttrack.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    // ── Create refresh token for owner
    public RefreshToken createRefreshToken(Owner owner) {

        // ── Revoke existing token if any
        refreshTokenRepository.revokeAllByOwnerId(owner.getId());

        // ── Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());  // ← Random UUID
        refreshToken.setOwner(owner);
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    // ── Validate refresh token
    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException(
                        "Invalid refresh token"
                ));

        // ── Check if revoked
        if (refreshToken.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token has been revoked. Please login again"
            );
        }

        // ── Check if expired
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            // ── Auto revoke expired token
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException(
                    "Refresh token expired. Please login again"
            );
        }

        return refreshToken;
    }

    // ── Revoke all tokens on logout
    public void revokeAllTokens(Integer ownerId) {
        refreshTokenRepository.revokeAllByOwnerId(ownerId);
    }
}