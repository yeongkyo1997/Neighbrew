package com.ssafy.backend.authentication.infra.google;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.domain.template.AbstractOAuthApiClient;
import com.ssafy.backend.authentication.infra.google.GoogleInfoResponse.Response;

@Component
public class RefactoredGoogleApiClient extends AbstractOAuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE = "email profile";
    @Value("${oauth.google.url.auth}")
    private String authUrl;
    @Value("${oauth.google.url.api}")
    private String apiUrl;
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client_secret}")
    private String clientSecret;
    @Value("${oauth.google.url.redirect}")
    private String redirectUri;

    public RefactoredGoogleApiClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    static OAuthInfoResponse getoAuthInfoResponse(String url, HttpEntity<?> request,
                                                  RestTemplate restTemplate) {
        ResponseEntity<Response> response = restTemplate.exchange(url, HttpMethod.GET, request, Response.class);

        Response responseBody = response.getBody();
        if (responseBody != null) {
            return new GoogleInfoResponse(responseBody);
        }

        return null;
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    protected String getTokenUrl() {
        return apiUrl;
    }

    @Override
    protected String getUserInfoUrl(String accessToken) {
        return "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken;
    }

    @Override
    protected MultiValueMap<String, String> createTokenRequestBody(OAuthLoginParams params) {
        String decodedCode = URLDecoder.decode(params.code(), StandardCharsets.UTF_8);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", decodedCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", GRANT_TYPE);

        return body;
    }

    @Override
    protected HttpEntity<?> createUserInfoRequest(String accessToken) {
        return new HttpEntity<>(new HttpHeaders());
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = getUserInfoUrl(accessToken);
        HttpEntity<?> request = createUserInfoRequest(accessToken);

        return getoAuthInfoResponse(url, request, restTemplate);
    }

    @Override
    protected Class<?> getTokenResponseType() {
        return GoogleTokens.class;
    }

    @Override
    protected Class<?> getUserInfoResponseType() {
        return GoogleInfoResponse.Response.class;
    }

    @Override
    protected String extractAccessToken(Object tokenResponse) {
        if (tokenResponse instanceof GoogleTokens) {
            return ((GoogleTokens) tokenResponse).getAccessToken();
        }
        throw new IllegalArgumentException("Invalid token response type");
    }

    @Override
    protected OAuthInfoResponse processUserInfoResponse(Object userInfoResponse) {
        if (userInfoResponse instanceof GoogleInfoResponse.Response) {
            return new GoogleInfoResponse((GoogleInfoResponse.Response) userInfoResponse);
        }
        throw new IllegalArgumentException("Invalid user info response type");
    }

    @Override
    protected String buildAuthorizationUrl() {
        return authUrl + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type="
               + RESPONSE_TYPE + "&scope=" + SCOPE;
    }
}