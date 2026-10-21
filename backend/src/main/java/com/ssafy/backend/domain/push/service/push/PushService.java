package com.ssafy.backend.domain.push.service.push;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.service.push.manager.PushDataManager;
import com.ssafy.backend.domain.push.service.push.manager.SseConnectionManager;
import com.ssafy.backend.domain.push.service.push.manager.SseEventManager;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushService {
    private final SseConnectionManager connectionManager;
    private final SseEventManager eventManager;
    private final PushDataManager dataManager;

    public SseEmitter connect(Long userId, String lastEventId) {
        SseConnectionManager.ConnectionResult connection = connectionManager.createConnection(userId);
        SseEmitter sseEmitter = connection.getEmitter();
        String emitterId = connection.getEmitterId();

        eventManager.sendInitialEvent(sseEmitter, userId, emitterId);

        if (!lastEventId.isEmpty()) {
            eventManager.sendLostData(lastEventId, userId, emitterId, sseEmitter);
            eventManager.clearLostData(userId);
        }

        return sseEmitter;
    }

    public void send(User sender, User receiver, PushType pushType, String content, String url) {
        Push push = dataManager.createAndSavePush(sender, receiver, pushType, content, url);
        eventManager.broadcastToUser(receiver.getUserId(), push.toDto());
    }

    public void deletePushLog(PushType pushType, Long senderId, Long receiverId) {
        dataManager.deletePushLog(pushType, senderId, receiverId);
    }

    public List<Push> getUserPushLog(Long userId) {
        return dataManager.getUserPushLog(userId);
    }

    public void deletePushById(Long pushId) {
        dataManager.deletePushById(pushId);
    }
}