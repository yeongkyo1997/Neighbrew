package com.ssafy.backend.authentication.application;

import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.backend.authentication.infra.google.GoogleLoginParams;
import com.ssafy.backend.authentication.infra.kakao.KakaoLoginParams;
import com.ssafy.backend.authentication.infra.naver.NaverLoginParams;
import com.ssafy.backend.domain.push.service.push.PushService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;
    private final PushService pushService;

    //사용자가 동의 후 여기에 넣으면 됨
    @PostMapping("/kakao")
    public ResponseEntity<?> loginKakao(@RequestBody KakaoLoginParams params)
            throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @PostMapping("/naver")
    public ResponseEntity<?> loginNaver(@RequestBody NaverLoginParams params)
            throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleLoginParams params)
            throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<Map<String, String>> loginRequset(KakaoLoginParams params) {
        Map<String, String> response = new HashMap<>();
        response.put("URL", oAuthLoginService.redirectApiUrl(params));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/naver")
    public ResponseEntity<Map<String, String>> loginRequsetResponse(NaverLoginParams params) {
        Map<String, String> response = new HashMap<>();
        response.put("URL", oAuthLoginService.redirectApiUrl(params));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> loginRequsetResponse(GoogleLoginParams params) {
        Map<String, String> response = new HashMap<>();
        response.put("URL", oAuthLoginService.redirectApiUrl(params));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/connect/{userId}", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter connect(@PathVariable Long userId,
                              @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "")
                              String lastEventId,
                              @RequestParam(value = "lastEventId", required = false, defaultValue = "")
                              String lastEventIdParam) throws IOException {
        return pushService.connect(userId, lastEventId, lastEventIdParam);
    }
}
