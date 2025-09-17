package com.ssafy.backend.domain.follow.service.follow.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowPushManager {
    private final PushService pushService;
    private final PushRepository pushRepository;

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

    public void sendFollowNotification(User follower, User following) {
        pushService.send(follower, following, PushType.FOLLOW,
                         follower.getNickname() + "님이 회원님을 팔로우하기 시작했습니다.",
                         neighbrewUrl + "/mypage/" + follower.getUserId());
    }

    public void removeFollowNotification(Long followerId, Long followingId) {
        pushRepository.deleteByPushTypeAndSenderUserIdAndReceiverUserId(PushType.FOLLOW, followerId,
                                                                        followingId);
    }
}