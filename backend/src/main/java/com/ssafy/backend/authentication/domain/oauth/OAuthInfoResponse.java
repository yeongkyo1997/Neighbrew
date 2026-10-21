package com.ssafy.backend.authentication.domain.oauth;

public interface OAuthInfoResponse {
    // 회원 정보 API 응답
    // provider 안에서 사용자를 구분하는 고유 ID (이메일 제공에 동의하지 않아도 항상 내려옴)
    String getOAuthId();

    String getEmail();

    String getName();

    OAuthProvider getOAuthProvider();
}
