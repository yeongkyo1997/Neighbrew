package com.ssafy.backend.domain.drink.service.subreview.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.entity.SubReview;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.drink.repository.SubReviewRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.service.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubReviewValidator {
    private final UserReader userReader;
    private final DrinkReviewRepository drinkReviewRepository;
    private final SubReviewRepository subReviewRepository;

    public User validateAndGetUser(Long userId) {
        return userReader.getById(userId);
    }

    public DrinkReview validateAndGetDrinkReview(Long drinkReviewId) {
        return drinkReviewRepository.findById(drinkReviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
    }

    public SubReview validateAndGetSubReview(Long subReviewId) {
        return subReviewRepository.findById(subReviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
    }

    public void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("댓글 내용이 비어있습니다.");
        }
    }

    public void validateOwnership(SubReview subReview, User user) {
        if (!subReview.getUser().equals(user)) {
            throw new IllegalArgumentException("댓글 작성자와 요청자가 다릅니다.");
        }
    }
}