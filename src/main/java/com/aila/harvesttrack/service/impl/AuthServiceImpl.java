package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.*;
import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.model.RefreshToken;
import com.aila.harvesttrack.repository.OwnerRepository;
import com.aila.harvesttrack.security.JwtUtil;
import com.aila.harvesttrack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private OwnerRepository     ownerRepository;
    @Autowired private PasswordEncoder     passwordEncoder;
    @Autowired private JwtUtil             jwtUtil;
    @Autowired private RefreshTokenService refreshTokenService;

    // ── REGISTER
    @Override
    public AuthResponseDto register(RegisterRequestDto dto) {

        if (ownerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException(
                    "Phone " + dto.getPhone() + " is already registered"
            );
        }

        Owner owner = new Owner();
        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setPassword(passwordEncoder.encode(dto.getPassword()));

        Owner savedOwner = ownerRepository.save(owner);

        // ── Generate both tokens
        String       accessToken  = jwtUtil.generateToken(
                savedOwner.getId(), savedOwner.getPhone()
        );
        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(savedOwner);

        return new AuthResponseDto(
                accessToken,
                refreshToken.getToken(),
                savedOwner.getId(),
                savedOwner.getName(),
                savedOwner.getPhone(),
                savedOwner.getAddress()
        );
    }

    // ── LOGIN
    @Override
    public AuthResponseDto login(LoginRequestDto dto) {

        Owner owner = ownerRepository
                .findByPhoneAndDeletedAtIsNull(dto.getPhone())
                .orElseThrow(() -> new RuntimeException(
                        "Phone number not registered"
                ));

        if (!passwordEncoder.matches(dto.getPassword(), owner.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // ── Generate both tokens
        String       accessToken  = jwtUtil.generateToken(
                owner.getId(), owner.getPhone()
        );
        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(owner);

        return new AuthResponseDto(
                accessToken,
                refreshToken.getToken(),
                owner.getId(),
                owner.getName(),
                owner.getPhone(),
                owner.getAddress()
        );
    }

    // ── REFRESH TOKEN
    @Override
    public RefreshTokenResponseDto refreshToken(String token) {

        // ── Validate refresh token
        RefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(token);

        Owner owner = refreshToken.getOwner();

        // ── Generate new access token
        String newAccessToken = jwtUtil.generateToken(
                owner.getId(), owner.getPhone()
        );

        return new RefreshTokenResponseDto(
                newAccessToken,
                refreshToken.getToken()   // ← Same refresh token
        );
    }

    // ── LOGOUT
    @Override
    public void logout(Integer ownerId) {
        refreshTokenService.revokeAllTokens(ownerId);
    }
}