package com.ssafy.backend.authentication.domain.strategy;

import org.springframework.stereotype.Component;

import com.ssafy.backend.authentication.application.LoginResponse;
import com.ssafy.backend.authentication.domain.factory.OAuthClientFactory;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationStrategy implements AuthenticationStrategy {
    private final OAuthClientFactory clientFactory;

    @Override
    public LoginResponse authenticate(OAuthLoginParams params) {

        return createLoginResponse();
    }

    @Override
    public boolean supports(String provider) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());
            return clientFactory.isSupported(oAuthProvider);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getAuthorizationUrl() {
        throw new UnsupportedOperationException("Provider must be specified for OAuth authorization URL");
    }

    private LoginResponse createLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(generateAccessToken());
        response.setRefreshToken(generateRefreshToken());
        return response;
    }

    private String generateAccessToken() {
        return "mock_access_token";
    }

    private String generateRefreshToken() {
        return "mock_refresh_token";
    }
}