package com.ssafy.backend.exception;

import static org.springframework.web.client.HttpClientErrorException.Unauthorized;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // provider 응답 본문은 내부 정보가 섞일 수 있으므로 고정 메시지만 내려준다.
    @ExceptionHandler(InvalidOAuthCodeException.class)
    public ResponseEntity<String> handleInvalidOAuthCodeException(InvalidOAuthCodeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("소셜 로그인 인증이 만료되었습니다. 다시 로그인해 주세요.");
    }

    @ExceptionHandler(OAuthProviderException.class)
    public ResponseEntity<String> handleOAuthProviderException(OAuthProviderException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("소셜 로그인 서비스에 일시적인 문제가 있습니다. 잠시 후 다시 시도해 주세요.");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러: " + e.getMessage());
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<String> handlerAsyncRequestTimeoutException(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Push 알림 서버를 재 연결합니다.");
    }

    @ExceptionHandler({
            MalformedJwtException.class, SignatureException.class, ExpiredJwtException.class, JwtException.class,
            Unauthorized.class
    })
    public ResponseEntity<?> handleJwtException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
