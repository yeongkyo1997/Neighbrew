package com.ssafy.backend.authentication.infra.kakao;

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
public class KakaoApiClient extends AbstractOAuthApiClient {
    private static final String GRANT_TYPE = "authorization_code";
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.url.redirect}")
    private String redirectUri;

    public KakaoApiClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    protected String tokenUrl() {
        return authUrl + "/oauth/token";
    }

    @Override
    protected MultiValueMap<String, String> tokenRequestBody(OAuthLoginParams params) {
        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        return body;
    }

    @Override
    protected Class<? extends OAuthTokens> tokenResponseType() {
        return KakaoTokens.class;
    }

    @Override
    protected OAuthInfoResponse fetchUserInfo(String accessToken) {
        HttpHeaders httpHeaders = bearerHeaders(accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

        return restTemplate.postForObject(apiUrl + "/v2/user/me", new HttpEntity<>(body, httpHeaders),
                                          KakaoInfoResponse.class);
    }

    @Override
    public String authApiUrl(OAuthLoginParams params) {
        String responseType = "code";
        return authUrl + "/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri
               + "&response_type=" + responseType;
    }
}
