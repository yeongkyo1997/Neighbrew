package com.ssafy.backend.domain.user.service.commentlike;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.entity.UserCommentLike;
import com.ssafy.backend.domain.user.service.commentlike.manager.UserCommentLikeManager;
import com.ssafy.backend.domain.user.service.commentlike.manager.UserCommentLikePushManager;
import com.ssafy.backend.domain.user.service.commentlike.manager.UserCommentLikeQueryManager;
import com.ssafy.backend.domain.user.service.commentlike.validator.UserCommentLikeValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCommentLikeService {
    private final UserCommentLikeValidator validator;
    private final UserCommentLikeQueryManager queryManager;
    private final UserCommentLikeManager likeManager;
    private final UserCommentLikePushManager pushManager;

    @Transactional
    public boolean toggleUserLike(Long userId, Long reviewId) {
        User user = validator.validateAndGetUser(userId);
        DrinkReview drinkReview = validator.validateAndGetDrinkReview(reviewId);

        Optional<UserCommentLike> existingLike = queryManager.findLike(userId, reviewId);

        if (existingLike.isPresent()) {
            likeManager.deleteLike(existingLike.get(), drinkReview);
            return false;
        } else {
            likeManager.createLike(user, drinkReview);
            pushManager.sendLikeNotification(user, drinkReview, reviewId);
            return true;
        }
    }

    public boolean getIsLike(Long userId, Long reviewId) {
        // 좋아요 행은 사용자와 리뷰가 모두 있어야 존재하므로, 존재 여부 조회 한 번으로 충분하다.
        return queryManager.isLiked(userId, reviewId);
    }
}