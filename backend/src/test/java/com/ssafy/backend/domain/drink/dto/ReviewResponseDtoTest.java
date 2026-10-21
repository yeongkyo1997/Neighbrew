package com.ssafy.backend.domain.drink.dto;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;

import org.junit.jupiter.api.Test;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.entity.SubReview;

class ReviewResponseDtoTest {
    @Test
    void 술_리뷰_작성자는_공개_프로필만_담는다() {
        DrinkReview review = DrinkReview.builder().drinkReviewId(1L).user(emailNicknameUser(7L))
                                        .drink(Drink.builder().drinkId(2L).name("막걸리").build()).content("맛있다")
                                        .likeCount(0L).build();

        assertPublicUser(toJson(DrinkReviewResponseDto.fromEntity(review)).get("user"), 7L, "user_7");
    }

    @Test
    void 댓글_작성자는_공개_프로필만_담는다() {
        SubReview subReview = SubReview.builder().subReviewId(1L).content("동의").user(emailNicknameUser(7L)).build();

        assertPublicUser(toJson(SubReviewResponseDto.fromEntity(subReview)).get("user"), 7L, "user_7");
    }
}
