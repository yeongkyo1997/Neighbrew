package com.ssafy.backend.domain.drink.dto;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DrinkReviewRequestDto {
    private Long userId;
    private Long drinkId;
    private String content;
    private String img;

    public DrinkReview toEntity(User user, Drink drink) {
        return DrinkReview.builder()
                          .user(user)
                          .drink(drink)
                          .content(content)
                          .img(img)
                          .build();
    }
}
