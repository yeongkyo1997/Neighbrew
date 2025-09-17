package com.ssafy.backend.domain.drink.service.drink.review.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.DrinkReviewRequestDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkReviewManager {
    private final DrinkReviewRepository drinkReviewRepository;

    public DrinkReview createDrinkReview(DrinkReviewRequestDto requestDto, User user, Drink drink,
                                         String imagePath) {
        requestDto.setImg(imagePath);
        DrinkReview drinkReview = requestDto.toEntity(user, drink);
        return drinkReviewRepository.save(drinkReview);
    }

    public DrinkReview updateDrinkReviewContent(DrinkReview drinkReview, String content, String imagePath) {
        drinkReview.updateContent(content);
        drinkReview.updateImg(imagePath);
        return drinkReviewRepository.save(drinkReview);
    }

    public void deleteDrinkReview(Long drinkReviewId) {
        drinkReviewRepository.deleteById(drinkReviewId);
    }

    public DrinkReview saveDrinkReview(DrinkReview drinkReview) {
        return drinkReviewRepository.save(drinkReview);
    }
}