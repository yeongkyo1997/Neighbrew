package com.ssafy.backend.domain.user.service.commentlike.manager;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.entity.UserCommentLike;
import com.ssafy.backend.domain.user.repository.UserCommentLikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCommentLikeManager {
    private final UserCommentLikeRepository userCommentLikeRepository;
    private final DrinkReviewRepository drinkReviewRepository;

    @Transactional
    public void createLike(User user, DrinkReview drinkReview) {
        UserCommentLike like = UserCommentLike.builder().user(user).drinkReview(drinkReview).build();

        userCommentLikeRepository.save(like);
        drinkReview.increaseLikeCount();
        drinkReviewRepository.save(drinkReview);
    }

    @Transactional
    public void deleteLike(UserCommentLike like, DrinkReview drinkReview) {
        userCommentLikeRepository.delete(like);
        drinkReview.decreaseLikeCount();
        drinkReviewRepository.save(drinkReview);
    }
}