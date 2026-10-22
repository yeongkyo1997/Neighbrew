package com.ssafy.backend.domain.user.service.commentlike.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.service.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCommentLikeValidator {
    private final UserReader userReader;
    private final DrinkReviewRepository drinkReviewRepository;

    public User validateAndGetUser(Long userId) {
        return userReader.getById(userId);
    }

    public DrinkReview validateAndGetDrinkReview(Long reviewId) {
        return drinkReviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 없습니다. id=" + reviewId));
    }
}