package com.ssafy.backend.domain.drink.dto;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.entity.SubReview;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubReviewRequestDto {
    private Long subReviewId;
    private String content;
    private Long drinkReviewId;
    private Long userId;

    public SubReview toEntity(User user, DrinkReview drinkReview) {
        return SubReview.builder().user(user).drinkReview(drinkReview).content(content).build();
    }
}
