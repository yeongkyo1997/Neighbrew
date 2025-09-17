package com.ssafy.backend.authentication.application.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.backend.authentication.application.LoginResponse;
import com.ssafy.backend.authentication.application.service.OAuthService;
import com.ssafy.backend.authentication.application.service.TokenService;
import com.ssafy.backend.authentication.application.service.UserService;
import com.ssafy.backend.authentication.domain.AuthTokens;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFacade {
    private final OAuthService oAuthService;
    private final UserService userService;
    private final TokenService tokenService;

    @Transactional
    public LoginResponse login(OAuthLoginParams params) {
        try {
            log.debug("OAuth 인증 시작: provider={}", params.oAuthProvider());

            OAuthInfoResponse oAuthInfo = oAuthService.authenticate(params);

            Long userId = userService.findOrCreateUser(oAuthInfo);

            AuthTokens tokens = tokenService.generateTokens(userId);

            return buildLoginResponse(userId, tokens);

        } catch (Exception e) {
            log.error("OAuth 로그인 실패", e);
            throw new RuntimeException("인증 처리 중 오류가 발생했습니다", e);
        }
    }

    public String getAuthorizationUrl(OAuthLoginParams params) {
        validateProvider(params);
        return oAuthService.getAuthorizationUrl(params);
    }

    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        try {
            AuthTokens newTokens = tokenService.refreshTokens(refreshToken);

            String userId = tokenService.extractUserId(refreshToken);

            return buildLoginResponse(Long.parseLong(userId), newTokens);

        } catch (Exception e) {
            log.error("토큰 갱신 실패", e);
            throw new RuntimeException("토큰 갱신 중 오류가 발생했습니다", e);
        }
    }

    private void validateProvider(OAuthLoginParams params) {
        if (!oAuthService.isProviderSupported(params)) {
            throw new IllegalArgumentException("지원하지 않는 OAuth Provider입니다: " + params.oAuthProvider());
        }
    }

    private LoginResponse buildLoginResponse(Long userId, AuthTokens tokens) {
        return LoginResponse.builder().userId(userId).accessToken(tokens.getAccessToken()).refreshToken(
                tokens.getRefreshToken()).build();
    }
}