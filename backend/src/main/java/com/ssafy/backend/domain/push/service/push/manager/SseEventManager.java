package com.ssafy.backend.domain.push.service.push.manager;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.domain.push.repository.EmitterRepository;
import com.ssafy.backend.domain.push.service.push.util.PushIdGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEventManager {
    private final PushIdGenerator idGenerator;
    private final EmitterRepository emitterRepository;

    private void sendEventToClient(SseEmitter sseEmitter, String eventId, String sseEmitterId, Object data) {
        try {
            sseEmitter.send(SseEmitter.event().id(eventId).data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(sseEmitterId);
            sseEmitter.completeWithError(e);
        }
    }

    public void sendInitialEvent(SseEmitter sseEmitter, Long userId, String sseEmitterId) {
        String eventId = idGenerator.makeTimeIncludeId(userId);
        sendEventToClient(sseEmitter, eventId, sseEmitterId, "EventStream Created. [userId= " + userId + "]");
    }

    public void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(
                String.valueOf(userId));

        eventCaches.entrySet().stream().filter(entry -> lastEventId.compareTo(entry.getKey()) < 0).forEach(
                entry -> sendEventToClient(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void clearLostData(Long userId) {
        emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(userId));
    }

    public void broadcastToUser(Long receiverId, Object pushData) {
        String eventId = idGenerator.makeTimeIncludeId(receiverId);
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(
                String.valueOf(receiverId));

        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, pushData);
            sendEventToClient(emitter, eventId, key, pushData);
        });
    }
}
