package com.ssafy.backend.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ssafy.backend.common.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {
    public Map<String, String> generateTokens(String userId) {
        String newAccessToken = JwtUtil.generateToken(userId);
        String newRefreshToken = JwtUtil.generateRefreshToken(userId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }

    public Claims getClaimsFromToken(String token) {
        return JwtUtil.getClaims(token);
    }
}
