package com.ssafy.backend.common.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {

    private static final String TOKEN_TYPE = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";
    private static final String BEARER_PREFIX = "Bearer ";

    // 재시작/다중 인스턴스에서도 토큰이 유지되도록 환경변수 JWT_SECRET(32바이트 이상)으로 서명 키를 고정한다.
    private static final Key secretKey = createKey();

    private static Key createKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.warn("JWT_SECRET 환경변수가 없거나 32바이트 미만입니다. 임시 키를 사용하므로 재시작 시 모든 토큰이 무효화됩니다.");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(String subject) {
        return Jwts.builder()
                   .setSubject(subject)
                   .claim(TOKEN_TYPE, ACCESS)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 60L * 1000 * 60 * 24)) // 24 hours
                   .signWith(secretKey)
                   .compact();
    }

    public static String generateRefreshToken(String subject) {
        return Jwts.builder()
                   .setSubject(subject)
                   .claim(TOKEN_TYPE, REFRESH)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                   .signWith(secretKey)
                   .compact();
    }

    // 검증로직을 실행하는 메서드 (access 토큰만 허용)
    public static Claims getClaims(String token) {
        return getClaimsOfType(token, ACCESS);
    }

    // refresh 토큰만 허용
    public static Claims getRefreshClaims(String token) {
        return getClaimsOfType(stripBearer(token), REFRESH);
    }

    private static Claims getClaimsOfType(String token, String type) {
        Claims claims = Jwts.parser()
                            .setSigningKey(secretKey)
                            .parseClaimsJws(token)
                            .getBody();
        if (!type.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new JwtException("토큰 종류가 올바르지 않습니다.");
        }
        return claims;
    }

    private static String stripBearer(String token) {
        return token != null && token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()) : token;
    }

    public static Long parseUserIdFromToken(String token) {
        Claims claims = getClaims(stripBearer(token));
        return Long.parseLong(claims.getSubject());
    }

    public static void validateToken(String token, Long userId) {
        Long subject = parseUserIdFromToken(token);
        if (!subject.equals(userId)) {
            throw new RuntimeException("토큰 정보가 유효하지 않습니다.");
        }
    }
}
