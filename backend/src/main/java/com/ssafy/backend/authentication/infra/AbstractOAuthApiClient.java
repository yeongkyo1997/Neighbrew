package com.ssafy.backend.authentication.infra;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthApiClient;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthTokens;
import com.ssafy.backend.exception.InvalidOAuthCodeException;
import com.ssafy.backend.exception.OAuthProviderException;

// provider 공통 흐름(토큰 발급 → 사용자 정보 조회)과 실패 처리를 한 곳에 둔다. (Template Method)
// 하위 클래스는 provider별 URL, 요청 본문, 응답 타입만 정한다.
public abstract class AbstractOAuthApiClient implements OAuthApiClient {
    protected final RestTemplate restTemplate;

    protected AbstractOAuthApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public final String requestAccessToken(OAuthLoginParams params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> request = new HttpEntity<>(tokenRequestBody(params), httpHeaders);

        OAuthTokens tokens;
        try {
            tokens = restTemplate.postForObject(tokenUrl(), request, tokenResponseType());
        } catch (HttpClientErrorException e) {
            // 4xx는 인가 코드가 만료되었거나 이미 쓰인 경우다
            throw new InvalidOAuthCodeException(oAuthProvider() + " 인가 코드가 거절되었습니다", e);
        } catch (RestClientException e) {
            throw new OAuthProviderException(oAuthProvider() + " 토큰 요청에 실패했습니다", e);
        }

        if (tokens == null || tokens.getAccessToken() == null || tokens.getAccessToken().isBlank()) {
            throw new OAuthProviderException(oAuthProvider() + " 토큰 응답이 비어 있습니다");
        }
        return tokens.getAccessToken();
    }

    @Override
    public final OAuthInfoResponse requestOauthInfo(String accessToken) {
        OAuthInfoResponse response;
        try {
            response = fetchUserInfo(accessToken);
        } catch (RestClientException e) {
            throw new OAuthProviderException(oAuthProvider() + " 사용자 정보 요청에 실패했습니다", e);
        }

        if (response == null) {
            throw new OAuthProviderException(oAuthProvider() + " 사용자 정보 응답이 비어 있습니다");
        }
        return response;
    }

    protected HttpHeaders bearerHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        return httpHeaders;
    }

    protected abstract String tokenUrl();

    protected abstract MultiValueMap<String, String> tokenRequestBody(OAuthLoginParams params);

    protected abstract Class<? extends OAuthTokens> tokenResponseType();

    protected abstract OAuthInfoResponse fetchUserInfo(String accessToken);
}
