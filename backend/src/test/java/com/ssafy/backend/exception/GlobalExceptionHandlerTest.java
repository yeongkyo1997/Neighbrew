package com.ssafy.backend.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void 거절된_인가_코드는_401로_응답하고_provider_메시지를_노출하지_않는다() {
        ResponseEntity<String> response = handler.handleInvalidOAuthCodeException(
                new InvalidOAuthCodeException("KAKAO 인가 코드가 거절되었습니다", new RuntimeException("provider body")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).doesNotContain("KAKAO", "provider body");
    }

    @Test
    void provider_오류는_502로_응답한다() {
        ResponseEntity<String> response = handler.handleOAuthProviderException(
                new OAuthProviderException("GOOGLE 토큰 응답이 비어 있습니다"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).doesNotContain("GOOGLE");
    }

    @Test
    void 대상이_없으면_404로_응답한다() {
        ResponseEntity<String> response = handler.handleNotFoundException(
                new NotFoundException("사용자를 찾을 수 없습니다. id=7"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("사용자를 찾을 수 없습니다. id=7");
    }
}
