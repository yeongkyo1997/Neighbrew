package com.ssafy.backend.authentication.application.service;

import org.springframework.stereotype.Service;

import com.ssafy.backend.authentication.domain.AuthTokens;
import com.ssafy.backend.common.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    public AuthTokens generateTokens(Long userId) {
        String userIdStr = String.valueOf(userId);
        return AuthTokens.builder().accessToken(generateAccessToken(userIdStr)).refreshToken(
                generateRefreshToken(userIdStr)).build();
    }

    public AuthTokens generateTokens(String userId) {
        return AuthTokens.builder().accessToken(generateAccessToken(userId)).refreshToken(
                generateRefreshToken(userId)).build();
    }

    public String generateAccessToken(String userId) {
        return JwtUtil.generateToken(userId);
    }

    public String generateRefreshToken(String userId) {
        return JwtUtil.generateRefreshToken(userId);
    }

    public boolean validateToken() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return JwtUtil.getRefreshClaims(token).getSubject();
    }

    public AuthTokens refreshTokens(String refreshToken) {
        if (!validateToken()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = extractUserId(refreshToken);
        return generateTokens(userId);
    }
}