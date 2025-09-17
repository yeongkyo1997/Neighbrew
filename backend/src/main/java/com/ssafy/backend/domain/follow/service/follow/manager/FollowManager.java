package com.ssafy.backend.domain.follow.service.follow.manager;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.follow.entity.Follow;
import com.ssafy.backend.domain.follow.repository.FollowRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowManager {
    private final FollowRepository followRepository;
    private final FollowPushManager pushManager;

    public String toggleFollow(User follower, User following) {
        Optional<Follow> existingFollow = followRepository.findByFollowerUserIdAndFollowingUserId(
                follower.getUserId(), following.getUserId());

        if (existingFollow.isPresent()) {
            unfollowUser(existingFollow.get(), follower.getUserId(), following.getUserId());
            return following.getNickname() + "님을 팔로우 취소하였습니다.";
        } else {
            followUser(follower, following);
            return following.getNickname() + "님을 팔로우하였습니다.";
        }
    }

    private void unfollowUser(Follow existingFollow, Long followerId, Long followingId) {
        followRepository.delete(existingFollow);
        pushManager.removeFollowNotification(followerId, followingId);
    }

    private void followUser(User follower, User following) {
        Follow follow = Follow.builder()
                              .follower(follower)
                              .following(following)
                              .build();
        followRepository.save(follow);
        pushManager.sendFollowNotification(follower, following);
    }
}