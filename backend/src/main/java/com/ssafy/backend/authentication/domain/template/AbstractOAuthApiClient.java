package com.ssafy.backend.authentication.domain.template;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthApiClient;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;

public abstract class AbstractOAuthApiClient implements OAuthApiClient {
    protected final RestTemplate restTemplate;

    protected AbstractOAuthApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public abstract OAuthProvider oAuthProvider();

    @Override
    public String requestAccessToken(OAuthLoginParams params) throws UnsupportedEncodingException {
        String url = getTokenUrl();
        HttpHeaders headers = createTokenRequestHeaders();
        MultiValueMap<String, String> body = createTokenRequestBody(params);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        return extractAccessToken(restTemplate.postForObject(url, request, getTokenResponseType()));
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = getUserInfoUrl(accessToken);
        HttpEntity<?> request = createUserInfoRequest(accessToken);

        return processUserInfoResponse(restTemplate.postForObject(url, request, getUserInfoResponseType()));
    }

    @Override
    public String authApiUrl(OAuthLoginParams params) {
        return buildAuthorizationUrl();
    }

    protected HttpHeaders createTokenRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    protected HttpHeaders createUserInfoRequestHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    protected abstract String getTokenUrl();

    protected abstract String getUserInfoUrl(String accessToken);

    protected abstract MultiValueMap<String, String> createTokenRequestBody(OAuthLoginParams params)
            throws UnsupportedEncodingException;

    protected abstract HttpEntity<?> createUserInfoRequest(String accessToken);

    protected abstract Class<?> getTokenResponseType();

    protected abstract Class<?> getUserInfoResponseType();

    protected abstract String extractAccessToken(Object tokenResponse);

    protected abstract OAuthInfoResponse processUserInfoResponse(Object userInfoResponse);

    protected abstract String buildAuthorizationUrl();
}