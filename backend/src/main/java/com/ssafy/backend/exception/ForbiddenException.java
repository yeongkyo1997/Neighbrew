package com.ssafy.backend.exception;

// 토큰의 사용자와 요청 대상 사용자가 다를 때 사용 (403)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
