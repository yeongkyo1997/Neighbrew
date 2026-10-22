package com.ssafy.backend.domain.push.service.push.manager;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.domain.push.dto.PushDto;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.repository.EmitterRepository;
import com.ssafy.backend.domain.push.repository.PushRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// SSE 이벤트 ID는 DB에 저장된 Push의 pushId다.
// 재연결할 때 마지막으로 받은 ID(Last-Event-ID) 이후의 알림을 DB에서 다시 보내므로, 서버를 재시작해도 놓친 알림을 받을 수 있다.
@Component
@Slf4j
@RequiredArgsConstructor
public class SseEventManager {
    private final EmitterRepository emitterRepository;
    private final PushRepository pushRepository;

    private void sendEventToClient(SseEmitter sseEmitter, String sseEmitterId, SseEmitter.SseEventBuilder event) {
        try {
            sseEmitter.send(event);
        } catch (IOException e) {
            emitterRepository.deleteById(sseEmitterId);
            sseEmitter.completeWithError(e);
        }
    }

    // 연결 확인 이벤트에는 ID를 붙이지 않는다. 붙이면 브라우저의 lastEventId가 알림 ID가 아닌 값으로 덮어써진다.
    public void sendInitialEvent(SseEmitter sseEmitter, Long userId, String sseEmitterId) {
        sendEventToClient(sseEmitter, sseEmitterId,
                          SseEmitter.event().data("EventStream Created. [userId= " + userId + "]"));
    }

    // 오래 끊겨 있던 사용자에게 알림이 쏟아지지 않도록 최대 50개만 다시 보낸다 (나머지는 알림 페이지 목록에서 볼 수 있다)
    public void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Long lastPushId = parsePushId(lastEventId);
        if (lastPushId == null) {
            return;
        }
        for (Push push : pushRepository.findTop50ByReceiver_UserIdAndPushIdGreaterThanOrderByPushIdAsc(userId,
                                                                                                        lastPushId)) {
            sendEventToClient(emitter, emitterId, pushEvent(push.toDto()));
        }
    }

    public void broadcastToUser(Long receiverId, PushDto push) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(
                String.valueOf(receiverId));
        sseEmitters.forEach((key, emitter) -> sendEventToClient(emitter, key, pushEvent(push)));
    }

    private SseEmitter.SseEventBuilder pushEvent(PushDto push) {
        return SseEmitter.event().id(String.valueOf(push.getPushId())).data(push);
    }

    // 예전 형식(userId_시각)이나 잘못된 값이면 재전송하지 않는다
    private Long parsePushId(String lastEventId) {
        if (lastEventId == null || lastEventId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(lastEventId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
