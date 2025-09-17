package com.ssafy.backend.authentication.domain.oauth;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class RequestOAuthInfoService {
    //외부 API 요청의 중복 로직을 공통화
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toMap(OAuthApiClient::oAuthProvider, Function.identity()));
    }

    public OAuthInfoResponse request(OAuthLoginParams params) throws UnsupportedEncodingException {
        // 어떤 클라이언트인지 확인
        OAuthApiClient client = clients.get(params.oAuthProvider());

        //여기서 accessToken은 kakao, naver로부터 받아온 token
        String accessToken = client.requestAccessToken(params);

        return client.requestOauthInfo(accessToken);
    }

    public String authApiUrl(OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());
        return client.authApiUrl(params);
    }
}
