package com.ssafy.backend.domain.follow.service.follow.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.follow.dto.FollowResponseDto;
import com.ssafy.backend.domain.follow.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowQueryManager {
    private final FollowRepository followRepository;

    public List<FollowResponseDto> getFollowers(Long userId) {
        return followRepository.findByFollowingUserId(userId)
                               .orElseThrow(() -> new IllegalArgumentException("팔로워 정보를 찾을 수 없습니다."))
                               .stream()
                               .map(FollowResponseDto::fromEntity)
                               .collect(Collectors.toList());
    }

    public List<FollowResponseDto> getFollowing(Long userId) {
        return followRepository.findByFollowerUserId(userId)
                               .orElseThrow(() -> new IllegalArgumentException("팔로잉 정보를 찾을 수 없습니다."))
                               .stream()
                               .map(FollowResponseDto::fromEntity)
                               .collect(Collectors.toList());
    }
}