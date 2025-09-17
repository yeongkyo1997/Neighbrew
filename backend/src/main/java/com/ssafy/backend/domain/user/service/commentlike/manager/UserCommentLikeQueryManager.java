package com.ssafy.backend.domain.user.service.commentlike.manager;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.UserCommentLike;
import com.ssafy.backend.domain.user.repository.UserCommentLikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCommentLikeQueryManager {
    private final UserCommentLikeRepository userCommentLikeRepository;

    public Optional<UserCommentLike> findLike(Long userId, Long reviewId) {
        return userCommentLikeRepository.findByUserUserIdAndDrinkReviewDrinkReviewId(userId, reviewId);
    }

    public boolean isLiked(Long userId, Long reviewId) {
        return userCommentLikeRepository.findByUserUserIdAndDrinkReviewDrinkReviewId(userId, reviewId)
                                        .isPresent();
    }
}