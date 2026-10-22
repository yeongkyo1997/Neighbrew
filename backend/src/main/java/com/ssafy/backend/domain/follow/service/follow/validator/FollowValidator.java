package com.ssafy.backend.domain.follow.service.follow.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.service.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowValidator {
    private final UserReader userReader;

    public void validateUserExists(Long userId) {
        userReader.validateExists(userId);
    }

    public User validateAndGetUser(Long userId) {
        return userReader.getById(userId);
    }
}