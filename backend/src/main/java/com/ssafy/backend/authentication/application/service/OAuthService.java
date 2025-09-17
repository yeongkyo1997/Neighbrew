package com.ssafy.backend.authentication.application.service;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Service;

import com.ssafy.backend.authentication.domain.factory.OAuthClientFactory;
import com.ssafy.backend.authentication.domain.oauth.OAuthApiClient;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OAuthClientFactory clientFactory;

    public OAuthInfoResponse authenticate(OAuthLoginParams params) throws UnsupportedEncodingException {
        OAuthApiClient client = clientFactory.getClient(params.oAuthProvider());

        String accessToken = client.requestAccessToken(params);

        return client.requestOauthInfo(accessToken);
    }

    public String getAuthorizationUrl(OAuthLoginParams params) {
        OAuthApiClient client = clientFactory.getClient(params.oAuthProvider());
        return client.authApiUrl(params);
    }

    public boolean isProviderSupported(OAuthLoginParams params) {
        return clientFactory.isSupported(params.oAuthProvider());
    }
}