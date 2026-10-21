package com.ssafy.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.domain.user.service.commentlike.UserCommentLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class UserCommentLikeController {
    private final UserCommentLikeService userCommentLikeService;

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getIsLik(@PathVariable Long reviewId,
                                      @LoginUser Long userId) {
        return ResponseEntity.ok(userCommentLikeService.getIsLike(userId, reviewId));
    }

    @PostMapping("/{reviewId}")
    public ResponseEntity<String> userLikeReview(@LoginUser Long userId,
                                                 @PathVariable Long reviewId) {
        return userCommentLikeService.toggleUserLike(userId, reviewId) ? ResponseEntity.ok("좋아요 등록 성공") :
               ResponseEntity.ok("좋아요 취소 성공");
    }
}
