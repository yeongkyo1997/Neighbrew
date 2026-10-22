package com.ssafy.backend.domain.push.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.common.auth.LoginUserGuard;
import com.ssafy.backend.common.util.SseTicketStore;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.service.push.PushService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class PushController {
    private final PushService pushService;
    private final SseTicketStore sseTicketStore;

    // SSE 연결용 단기(30초)·일회용 티켓 발급 (Authorization 헤더로 인증된 요청만)
    @PostMapping("/sse-ticket")
    public Map<String, String> issueSseTicket(@LoginUser Long loginUserId) {
        return Collections.singletonMap("ticket", sseTicketStore.issue(String.valueOf(loginUserId)));
    }

    @GetMapping(value = "/connect/{userId}", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter connect(@PathVariable Long userId,
                              @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "")
                              String lastEventId,
                              @RequestParam(value = "lastEventId", required = false, defaultValue = "")
                              String lastEventIdParam) throws IOException {
        //클라이언트로 전달되는 것이 아닌, 클라이언트에게 지속적으로 알림을 제공하기 위한 연결 통로를 생성한다.
        return pushService.connect(userId, lastEventId, lastEventIdParam);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Push> getUserPushLog(@PathVariable Long userId, @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return pushService.getUserPushLog(userId);
    }

    @DeleteMapping("/{pushId}")
    @ResponseStatus(HttpStatus.OK)
    public String deletePushLog(@PathVariable Long pushId, @LoginUser Long loginUserId) {
        pushService.deletePushById(pushId, loginUserId);
        return "알림이 삭제되었습니다.";
    }
}