package com.ssafy.backend.authentication.infra.naver;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthApiClient;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NaverApiClient implements OAuthApiClient {
    private static final String GRANT_TYPE = "authorization_code";
    @Qualifier("restTemplate")
    private final RestTemplate restTemplate;
    @Value("${oauth.naver.url.auth}")
    private String authUrl;
    @Value("${oauth.naver.url.api}")
    private String apiUrl;
    @Value("${oauth.naver.client-id}")
    private String clientId;
    @Value("${oauth.naver.secret}")
    private String clientSecret;
    // 설정이 없으면 기존 로컬 주소를 기본값으로 사용
    @Value("${oauth.naver.url.redirect:http://localhost:5173/naver/callback}")
    private String redirectUri;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth2.0/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverTokens response = restTemplate.postForObject(url, request, NaverTokens.class);

        assert response != null;

        return response.getAccessToken();
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = apiUrl + "/v1/nid/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        return restTemplate.postForObject(url, request, NaverInfoResponse.class);
    }

    @Override
    public String authApiUrl(OAuthLoginParams params) {
        String responseType = "code";
        // state에 client secret을 넣으면 브라우저에 노출되므로 매 요청마다 임의 값을 사용
        String state = UUID.randomUUID().toString();
        return authUrl + "/oauth2.0/authorize" + "?response_type=" + responseType + "&client_id=" + clientId
               + "&state=" + state + "&redirect_uri=" + redirectUri;
    }
}
