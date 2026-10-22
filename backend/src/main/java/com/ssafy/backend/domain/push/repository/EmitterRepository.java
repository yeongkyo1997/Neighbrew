package com.ssafy.backend.domain.push.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 회원별로 연결된 SseEmitter를 메모리에 보관한다. (서버 인스턴스가 하나라고 가정)
 * 놓친 알림은 DB의 Push로 다시 보내므로 이벤트는 여기에 저장하지 않는다.
 */
public interface EmitterRepository {
    //Emitter 저장
    SseEmitter save(String sseEmitterId, SseEmitter sseEmitter);

    //회원과 관련된 Emitter를 모두 찾음
    Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId);

    //Emitter를 지움
    void deleteById(String emitterId);
}
