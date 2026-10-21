package com.ssafy.backend.domain.push.service.push.manager;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PushDataManager {
    private final PushRepository pushRepository;

    public Push createAndSavePush(User sender, User receiver, PushType pushType, String content, String url) {
        Push push = createPush(sender, receiver, pushType, content, url);
        return pushRepository.save(push);
    }

    private Push createPush(User sender, User receiver, PushType pushType, String content, String url) {
        return Push.builder().receiver(receiver).sender(sender).pushType(pushType).content(content).url(url)
                   .isRead(false).build();
    }

    @Transactional
    public void deletePushLog(PushType pushType, Long senderId, Long receiverId) {
        pushRepository.deleteByPushTypeAndSenderUserIdAndReceiverUserId(pushType, senderId, receiverId);
    }

    public List<Push> getUserPushLog(Long userId) {
        return pushRepository.findByReceiver_UserIdOrderByCreatedAtDesc(userId);
    }

    // 본인이 받은 알림만 삭제할 수 있다. (없는 알림은 기존처럼 무시)
    @Transactional
    public void deletePushById(Long pushId, Long userId) {
        pushRepository.findById(pushId).ifPresent(push -> {
            if (!push.getReceiver().getUserId().equals(userId)) {
                throw new ForbiddenException("본인 알림만 삭제할 수 있습니다.");
            }
            pushRepository.delete(push);
        });
    }
}