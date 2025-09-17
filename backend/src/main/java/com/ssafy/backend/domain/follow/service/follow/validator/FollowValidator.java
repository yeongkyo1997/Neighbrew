package com.ssafy.backend.domain.follow.service.follow.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowValidator {
    private final UserRepository userRepository;

    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AssertionError("잘못된 userId입니다:" + userId);
        }
    }

    public User validateAndGetUser(Long userId, String errorPrefix) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(errorPrefix + userId));
    }
}