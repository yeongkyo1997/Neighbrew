package com.ssafy.backend.domain.user.service;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

// 다른 도메인에서 사용자를 조회할 때 쓰는 공용 Finder.
// 조회 정책(없을 때의 예외, 탈퇴 사용자 제외 등)은 여기서만 정한다.
@Component
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> notFound(userId));
    }

    public void validateExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw notFound(userId);
        }
    }

    private NotFoundException notFound(Long userId) {
        return new NotFoundException("사용자를 찾을 수 없습니다. id=" + userId);
    }
}
