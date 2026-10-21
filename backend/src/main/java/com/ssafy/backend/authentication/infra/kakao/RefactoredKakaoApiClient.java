package com.ssafy.backend.authentication.infra.kakao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.domain.template.AbstractOAuthApiClient;

// 기존 클라이언트와 같은 OAuthProvider를 반환하므로 빈으로 등록하지 않음(등록 시 Duplicate key로 기동 실패)
public class RefactoredKakaoApiClient extends AbstractOAuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.url.redirect}")
    private String redirectUri;

    public RefactoredKakaoApiClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    protected String getTokenUrl() {
        return authUrl + "/oauth/token";
    }

    @Override
    protected String getUserInfoUrl(String accessToken) {
        return apiUrl + "/v2/user/me";
    }

    @Override
    protected MultiValueMap<String, String> createTokenRequestBody(OAuthLoginParams params) {
        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        return body;
    }

    @Override
    protected HttpEntity<?> createUserInfoRequest(String accessToken) {
        HttpHeaders headers = createUserInfoRequestHeaders(accessToken);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");
        return new HttpEntity<>(body, headers);
    }

    @Override
    protected Class<?> getTokenResponseType() {
        return KakaoTokens.class;
    }

    @Override
    protected Class<?> getUserInfoResponseType() {
        return KakaoInfoResponse.class;
    }

    @Override
    protected String extractAccessToken(Object tokenResponse) {
        if (tokenResponse instanceof KakaoTokens) {
            return ((KakaoTokens) tokenResponse).getAccessToken();
        }
        throw new IllegalArgumentException("Invalid token response type");
    }

    @Override
    protected OAuthInfoResponse processUserInfoResponse(Object userInfoResponse) {
        if (userInfoResponse instanceof KakaoInfoResponse) {
            return (KakaoInfoResponse) userInfoResponse;
        }
        throw new IllegalArgumentException("Invalid user info response type");
    }

    @Override
    protected String buildAuthorizationUrl() {
        return authUrl + "/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri
               + "&response_type=" + RESPONSE_TYPE;
    }
}