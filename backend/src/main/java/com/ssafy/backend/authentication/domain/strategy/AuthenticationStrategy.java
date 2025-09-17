package com.ssafy.backend.authentication.domain.strategy;

import com.ssafy.backend.authentication.application.LoginResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;

public interface AuthenticationStrategy {
    LoginResponse authenticate(OAuthLoginParams params) throws Exception;

    boolean supports(String provider);

    String getAuthorizationUrl();
}