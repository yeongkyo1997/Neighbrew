package com.ssafy.backend.authentication.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode {
    INVALID_PROVIDER("AUTH001", "지원하지 않는 OAuth Provider입니다"), OAUTH_TOKEN_REQUEST_FAILED("AUTH002",
                                                                                         "OAuth 토큰 요청에 실패했습니다"),
    OAUTH_INFO_REQUEST_FAILED("AUTH003", "OAuth 사용자 정보 요청에 실패했습니다"), USER_CREATION_FAILED("AUTH004",
                                                                                          "사용자 생성에 실패했습니다"),
    TOKEN_GENERATION_FAILED("AUTH005", "토큰 생성에 실패했습니다"), INVALID_TOKEN("AUTH006", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED("AUTH007", "만료된 토큰입니다"), REFRESH_TOKEN_INVALID("AUTH008", "유효하지 않은 리프레시 토큰입니다"),
    AUTHENTICATION_FAILED("AUTH009", "인증에 실패했습니다"), HTTP_REQUEST_FAILED("AUTH010", "HTTP 요청에 실패했습니다");

    private final String code;
    private final String message;
}