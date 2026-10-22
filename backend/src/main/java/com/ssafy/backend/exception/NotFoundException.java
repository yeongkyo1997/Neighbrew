package com.ssafy.backend.exception;

// 요청한 대상(사용자 등)이 없을 때 사용 (404)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
