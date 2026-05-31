package com.aila.harvesttrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Generate token with expiry embedded
    public String generateToken(Integer ownerId, String phone) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(ownerId))
                .claim("phone", phone)
                .setIssuedAt(now)
                .setExpiration(expiry)          // ← Expiry stored inside token
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Extract owner ID
    public Integer extractOwnerId(String token) {
        return Integer.valueOf(extractClaims(token).getSubject());
    }

    // ── Extract phone
    public String extractPhone(String token) {
        return extractClaims(token).get("phone", String.class);
    }

    // ── Extract expiry time as milliseconds
    public long extractExpiryTime(String token) {
        return extractClaims(token)
                .getExpiration()
                .getTime();                     // ← Returns epoch milliseconds
    }

    // ── Check if token expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // ── Check if token expires within given milliseconds
    public boolean isTokenExpiringWithin(String token, long milliseconds) {
        long expiryTime  = extractExpiryTime(token);
        long currentTime = System.currentTimeMillis();
        long timeLeft    = expiryTime - currentTime;
        return timeLeft < milliseconds;    // ← True if expiring soon
    }

    // ── Validate token
    public boolean validateToken(String token, Integer ownerId) {
        return extractOwnerId(token).equals(ownerId)
                && !isTokenExpired(token);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}