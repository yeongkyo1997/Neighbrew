package com.ssafy.backend.domain.user.service.commentlike.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.push.service.push.PushMessage;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCommentLikePushManager {
    private final PushService pushService;

    public void sendLikeNotification(User likeUser, DrinkReview drinkReview, Long reviewId) {
        pushService.send(likeUser, drinkReview.getUser(),
                         PushMessage.reviewLike(likeUser.getNickname(), drinkReview.getDrink().getDrinkId(), reviewId));
    }
}