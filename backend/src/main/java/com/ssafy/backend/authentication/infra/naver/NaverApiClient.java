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

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.domain.oauth.OAuthTokens;
import com.ssafy.backend.authentication.infra.AbstractOAuthApiClient;

@Component
public class NaverApiClient extends AbstractOAuthApiClient {
    private static final String GRANT_TYPE = "authorization_code";
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

    public NaverApiClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    protected String tokenUrl() {
        return authUrl + "/oauth2.0/token";
    }

    @Override
    protected MultiValueMap<String, String> tokenRequestBody(OAuthLoginParams params) {
        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return body;
    }

    @Override
    protected Class<? extends OAuthTokens> tokenResponseType() {
        return NaverTokens.class;
    }

    @Override
    protected OAuthInfoResponse fetchUserInfo(String accessToken) {
        HttpHeaders httpHeaders = bearerHeaders(accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return restTemplate.postForObject(apiUrl + "/v1/nid/me",
                                          new HttpEntity<>(new LinkedMultiValueMap<String, String>(), httpHeaders),
                                          NaverInfoResponse.class);
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
