package com.ssafy.backend.authentication.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.authentication.application.LoginResponse;
import com.ssafy.backend.authentication.application.facade.AuthenticationFacade;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.infra.google.GoogleLoginParams;
import com.ssafy.backend.authentication.infra.kakao.KakaoLoginParams;
import com.ssafy.backend.authentication.infra.naver.NaverLoginParams;
import com.ssafy.backend.domain.push.service.push.PushService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@Slf4j
public class RefactoredAuthController {
    private final AuthenticationFacade authenticationFacade;
    private final PushService pushService;

    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String provider,
            @RequestBody Map<String, Object> params) {

        OAuthLoginParams loginParams = createLoginParams(provider, params);
        LoginResponse response = authenticationFacade.login(loginParams);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth/{provider}/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(
            @PathVariable String provider) {

        OAuthLoginParams loginParams = createEmptyLoginParams(provider);
        String authUrl = authenticationFacade.getAuthorizationUrl(loginParams);

        Map<String, String> response = new HashMap<>();
        response.put("authorizationUrl", authUrl);
        response.put("provider", provider);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken) {

        LoginResponse response = authenticationFacade.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/connect/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter connect(
            @PathVariable Long userId,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId)
            throws IOException {

        return pushService.connect(userId, lastEventId);
    }

    private OAuthLoginParams createLoginParams(String provider, Map<String, Object> params) {
        OAuthProvider oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());

        switch (oAuthProvider) {
            case KAKAO:
                KakaoLoginParams kakaoParams = new KakaoLoginParams();
                kakaoParams.setAuthorizationCode((String) params.get("code"));
                return kakaoParams;

            case NAVER:
                NaverLoginParams naverParams = new NaverLoginParams();
                naverParams.setAuthorizationCode((String) params.get("code"));
                naverParams.setState((String) params.get("state"));
                return naverParams;

            case GOOGLE:
                GoogleLoginParams googleParams = new GoogleLoginParams();
                googleParams.setAuthorizationCode((String) params.get("code"));
                return googleParams;

            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth Provider: " + provider);
        }
    }

    private OAuthLoginParams createEmptyLoginParams(String provider) {
        OAuthProvider oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());

        switch (oAuthProvider) {
            case KAKAO:
                return new KakaoLoginParams();
            case NAVER:
                return new NaverLoginParams();
            case GOOGLE:
                return new GoogleLoginParams();
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth Provider: " + provider);
        }
    }
}