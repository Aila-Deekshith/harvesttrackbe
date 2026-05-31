package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Integer> {

    // ── Find by token string
    Optional<RefreshToken> findByToken(String token);

    // ── Find active token by owner
    Optional<RefreshToken> findByOwnerIdAndRevokedFalse(Integer ownerId);

    // ── Revoke all tokens for owner (on logout)
    @Modifying
    @Transactional
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = true
        WHERE r.owner.id = :ownerId
    """)
    int revokeAllByOwnerId(@Param("ownerId") Integer ownerId);

    // ── Delete expired tokens (cleanup)
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM RefreshToken r
        WHERE r.expiresAt < CURRENT_TIMESTAMP
    """)
    int deleteExpiredTokens();
}