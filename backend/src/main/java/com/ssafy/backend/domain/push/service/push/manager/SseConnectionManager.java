package com.ssafy.backend.domain.push.service.push.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.domain.push.repository.EmitterRepositoryImpl;
import com.ssafy.backend.domain.push.service.push.util.PushIdGenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseConnectionManager {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60 * 24;

    @Getter
    private final EmitterRepositoryImpl emitterRepository = new EmitterRepositoryImpl();
    private final PushIdGenerator idGenerator;

    public ConnectionResult createConnection(Long userId) {
        String sseEmitterId = idGenerator.makeTimeIncludeId(userId);
        SseEmitter sseEmitter = emitterRepository.save(sseEmitterId, new SseEmitter(DEFAULT_TIMEOUT));

        setupEmitterCallbacks(sseEmitter, sseEmitterId);

        return new ConnectionResult(sseEmitter, sseEmitterId);
    }

    private void setupEmitterCallbacks(SseEmitter sseEmitter, String sseEmitterId) {
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(sseEmitterId));
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(sseEmitterId));
    }

    @Getter
    public static class ConnectionResult {
        private final SseEmitter emitter;
        private final String emitterId;

        public ConnectionResult(SseEmitter emitter, String emitterId) {
            this.emitter = emitter;
            this.emitterId = emitterId;
        }
    }
}