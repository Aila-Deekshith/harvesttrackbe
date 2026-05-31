package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.AuthResponseDto;
import com.aila.harvesttrack.dto.LoginRequestDto;
import com.aila.harvesttrack.dto.RefreshTokenResponseDto;
import com.aila.harvesttrack.dto.RegisterRequestDto;

public interface AuthService {

    // ── Register new owner
    AuthResponseDto register(RegisterRequestDto dto);

    // ── Login owner
    AuthResponseDto login(LoginRequestDto dto);

    // ── REFRESH TOKEN
    RefreshTokenResponseDto refreshToken(String token);

    // ── LOGOUT
    void logout(Integer ownerId);
}