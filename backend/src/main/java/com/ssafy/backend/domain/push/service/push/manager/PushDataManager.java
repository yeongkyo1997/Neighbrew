package com.ssafy.backend.domain.push.service.push.manager;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.user.entity.User;

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

    @Transactional
    public void deletePushById(Long pushId) {
        pushRepository.deleteById(pushId);
    }
}