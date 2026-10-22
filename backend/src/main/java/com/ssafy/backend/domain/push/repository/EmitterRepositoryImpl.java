package com.ssafy.backend.domain.push.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //Emitter 저장
    @Override
    public SseEmitter save(String sseEmitterId, SseEmitter sseEmitter) {
        emitters.put(sseEmitterId, sseEmitter);

        return sseEmitter;
    }

    //회원과 관련된 Emitter를 모두 찾음
    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        return emitters.entrySet().stream()
                       //sseEmitter의 key들은 userId_currentTime 형태여서 앞부분이 userId인 것만 찾게 한다.
                       .filter(entry -> entry.getKey().split("_")[0].equals(userId))
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //주어진 ID 와 Emitter를 제거
    @Override
    public void deleteById(String sseEmitterId) {
        emitters.remove(sseEmitterId);
    }
}
