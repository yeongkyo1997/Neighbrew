package com.ssafy.backend.domain.follow.service.follow.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.push.service.push.PushMessage;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowPushManager {
    private final PushService pushService;
    private final PushRepository pushRepository;

    public void sendFollowNotification(User follower, User following) {
        pushService.send(follower, following, PushMessage.follow(follower.getNickname(), follower.getUserId()));
    }

    public void removeFollowNotification(Long followerId, Long followingId) {
        pushRepository.deleteByPushTypeAndSenderUserIdAndReceiverUserId(PushType.FOLLOW, followerId,
                                                                        followingId);
    }
}