package com.ssafy.backend.domain.follow.service.follow;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.follow.dto.FollowResponseDto;
import com.ssafy.backend.domain.follow.service.follow.manager.FollowManager;
import com.ssafy.backend.domain.follow.service.follow.manager.FollowQueryManager;
import com.ssafy.backend.domain.follow.service.follow.validator.FollowValidator;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowValidator validator;
    private final FollowQueryManager queryManager;
    private final FollowManager followManager;

    public List<FollowResponseDto> getFollowers(Long userId) {
        validator.validateUserExists(userId);
        return queryManager.getFollowers(userId);
    }

    @Transactional
    public String toggleFollow(Long userId, Long followingId) {
        User follower = validator.validateAndGetUser(userId, "잘못된 userId입니다: ");
        User following = validator.validateAndGetUser(followingId, "잘못된 followingId입니다: ");
        return followManager.toggleFollow(follower, following);
    }

    public List<FollowResponseDto> getFollowing(Long userId) {
        validator.validateUserExists(userId);
        return queryManager.getFollowing(userId);
    }
}