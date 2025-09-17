package com.ssafy.backend.domain.drink.service.drink.review.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.DrinkReviewUpdateDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkReviewValidator {
    private final UserRepository userRepository;
    private final DrinkRepository drinkRepository;
    private final DrinkReviewRepository drinkReviewRepository;

    public User validateAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    public Drink validateAndGetDrink(Long drinkId) {
        return drinkRepository.findById(drinkId).orElseThrow(
                () -> new IllegalArgumentException("음료가 존재하지 않습니다."));
    }

    public DrinkReview validateAndGetDrinkReview(Long drinkReviewId) {
        return drinkReviewRepository.findById(drinkReviewId).orElseThrow(
                () -> new IllegalArgumentException("음료 리뷰가 존재하지 않습니다."));
    }

    public void validateReviewOwnership(DrinkReview drinkReview, Long userId) {
        if (!Objects.equals(drinkReview.getUser().getUserId(), userId)) {
            throw new IllegalArgumentException("해당 리뷰의 작성자가 아닙니다.");
        }
    }

    public void validateUpdateRequest(Long drinkReviewId, DrinkReviewUpdateDto request) {
        if (!drinkReviewId.equals(request.getDrinkReviewId())) {
            throw new IllegalArgumentException("요청한 리뷰 ID와 전송된 데이터의 리뷰 ID가 일치하지 않습니다.");
        }
    }

    public DrinkReview validateReviewByDrinkReviewId(Long drinkReviewId) {
        return drinkReviewRepository.findByDrinkReviewId(drinkReviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
    }
}