package com.ssafy.backend.domain.follow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.follow.dto.FollowResponseDto;
import com.ssafy.backend.domain.follow.service.follow.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    @GetMapping("/follower/{userId}")
    public ResponseEntity<List<FollowResponseDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowResponseDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }

    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@RequestHeader("Authorization") String token,
                                             @PathVariable Long followingId) {
        Long userId = JwtUtil.parseUserIdFromToken(token);
        return ResponseEntity.ok(followService.toggleFollow(userId, followingId));
    }
}
