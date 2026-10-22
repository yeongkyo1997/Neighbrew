package com.ssafy.backend.authentication.infra.google;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.domain.oauth.OAuthTokens;
import com.ssafy.backend.authentication.infra.AbstractOAuthApiClient;
import com.ssafy.backend.authentication.infra.google.GoogleInfoResponse.Response;

@Component
public class GoogleApiClient extends AbstractOAuthApiClient {
    @Value("${oauth.google.url.auth}")
    private String authUrl;
    // 토큰 발급 URL (포팅 매뉴얼의 oauth.google.url.api)
    @Value("${oauth.google.url.api}")
    private String apiUrl;
    @Value("${oauth.google.url.userinfo:https://www.googleapis.com/oauth2/v1/userinfo}")
    private String userInfoUrl;
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client_secret}")
    private String clientSecret;
    @Value("${oauth.google.url.redirect}")
    private String redirectUri;

    public GoogleApiClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    protected String tokenUrl() {
        return apiUrl;
    }

    @Override
    protected MultiValueMap<String, String> tokenRequestBody(OAuthLoginParams params) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", URLDecoder.decode(params.code(), StandardCharsets.UTF_8));
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        return body;
    }

    @Override
    protected Class<? extends OAuthTokens> tokenResponseType() {
        return GoogleTokens.class;
    }

    // 액세스 토큰을 쿼리스트링에 넣으면 프록시·서버 로그에 남으므로 Authorization 헤더로 보낸다.
    @Override
    protected OAuthInfoResponse fetchUserInfo(String accessToken) {
        Response body = restTemplate.exchange(userInfoUrl, HttpMethod.GET, new HttpEntity<>(bearerHeaders(accessToken)),
                                              Response.class).getBody();
        return body == null ? null : new GoogleInfoResponse(body);
    }

    @Override
    public String authApiUrl(OAuthLoginParams params) {
        String responseType = "code";
        return authUrl + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type="
               + responseType + "&scope=email profile";
    }
}
