package com.ssafy.backend.exception;

// OAuth provider가 인가 코드를 거절했을 때 사용 (401). 만료되었거나 이미 쓴 코드로 로그인한 경우
public class InvalidOAuthCodeException extends RuntimeException {
    public InvalidOAuthCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
