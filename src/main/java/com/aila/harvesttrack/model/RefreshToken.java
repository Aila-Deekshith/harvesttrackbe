package com.aila.harvesttrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ── The refresh token string
    @Column(nullable = false, unique = true)
    private String token;

    // ── Owner this token belongs to
    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private Owner owner;

    // ── Expiry time
    @Column(nullable = false)
    private Instant expiresAt;

    // ── Created time
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // ── Is this token revoked
    private boolean revoked = false;
}