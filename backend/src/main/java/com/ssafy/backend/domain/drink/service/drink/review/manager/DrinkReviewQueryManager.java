package com.ssafy.backend.domain.drink.service.drink.review.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.DrinkReviewResponseDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkReviewQueryManager {
    private final DrinkReviewRepository drinkReviewRepository;

    public Page<DrinkReviewResponseDto> findReviewsByDrinkId(Long drinkId, Pageable pageable) {
        Page<DrinkReview> drinkReviewPage = drinkReviewRepository.findByDrinkDrinkId(drinkId, pageable);
        return drinkReviewPage.map(DrinkReviewResponseDto::fromEntity);
    }

    public List<DrinkReviewResponseDto> findReviewsByUserAndDrink(User user, Drink drink) {
        List<DrinkReview> drinkReviewList = drinkReviewRepository.findAllByUserAndDrink(user, drink);
        return drinkReviewList.stream().map(DrinkReviewResponseDto::fromEntity).collect(Collectors.toList());
    }

    public Page<DrinkReviewResponseDto> findReviewsOrderByLikeCount(Pageable pageable) {
        Page<DrinkReview> drinkReviewPage = drinkReviewRepository.findAllByOrderByLikeCountDesc(pageable);
        return drinkReviewPage.map(DrinkReviewResponseDto::fromEntity);
    }

    public Page<DrinkReviewResponseDto> findReviewsOrderByCreatedAt(Pageable pageable) {
        Page<DrinkReview> drinkReviewPage = drinkReviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        return drinkReviewPage.map(DrinkReviewResponseDto::fromEntity);
    }

    public DrinkReviewResponseDto findReviewById(Long drinkReviewId) {
        DrinkReview drinkReview = drinkReviewRepository.findByDrinkReviewId(drinkReviewId).orElseThrow(
                () -> new NotFoundException("후기를 찾을 수 없습니다. id=" + drinkReviewId));
        return DrinkReviewResponseDto.fromEntity(drinkReview);
    }
}