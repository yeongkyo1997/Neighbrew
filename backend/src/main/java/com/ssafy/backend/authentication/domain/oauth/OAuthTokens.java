package com.ssafy.backend.authentication.domain.oauth;

// provider별 토큰 응답에서 공통으로 쓰는 값
public interface OAuthTokens {
    String getAccessToken();
}
