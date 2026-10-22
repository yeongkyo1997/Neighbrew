package com.ssafy.backend.exception;

// OAuth provider 응답이 비었거나 오류일 때 사용 (502). 클라이언트 입력 문제가 아니라 외부 서비스 문제다.
public class OAuthProviderException extends RuntimeException {
    public OAuthProviderException(String message) {
        super(message);
    }

    public OAuthProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
