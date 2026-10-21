package com.ssafy.backend.domain.drink.dto;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DrinkReviewResponseDto {
    public static DrinkReviewResponseDto fromEntity(DrinkReview drinkReview) {
        return DrinkReviewResponseDto.builder().drinkReviewId(drinkReview.getDrinkReviewId()).user(
                PublicUserResponseDto.fromEntity(drinkReview.getUser())).drink(drinkReview.getDrink()).content(drinkReview.getContent()).img(
                drinkReview.getImg()).likeCount(drinkReview.getLikeCount()).build();
    }

    private Long drinkReviewId;
    private PublicUserResponseDto user;
    private Drink drink;
    private String content;
    private String img;
    private Long likeCount;
}
