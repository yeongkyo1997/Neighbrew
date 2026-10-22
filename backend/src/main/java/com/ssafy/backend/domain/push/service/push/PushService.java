package com.ssafy.backend.domain.push.service.push;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

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

    // 이동 URL은 여기서만 도메인을 붙여 만든다. (호출하는 쪽마다 URL을 조립하면 형식이 어긋난다)
    public void send(User sender, User receiver, PushMessage message) {
        String url = stripTrailingSlash(neighbrewUrl) + message.path();
        Push push = dataManager.createAndSavePush(sender, receiver, message.pushType(), message.content(), url);
        eventManager.broadcastToUser(receiver.getUserId(), push.toDto());
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public void deletePushLog(PushType pushType, Long senderId, Long receiverId) {
        dataManager.deletePushLog(pushType, senderId, receiverId);
    }

    public List<Push> getUserPushLog(Long userId) {
        return dataManager.getUserPushLog(userId);
    }

    public void deletePushById(Long pushId, Long userId) {
        dataManager.deletePushById(pushId, userId);
    }
}