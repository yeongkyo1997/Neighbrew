package com.ssafy.backend.domain.user.service.commentlike.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCommentLikePushManager {
    private final PushService pushService;

    public void sendLikeNotification(User likeUser, DrinkReview drinkReview, Long reviewId) {
        pushService.send(likeUser, drinkReview.getUser(), PushType.REVIEWLIKE,
                         likeUser.getNickname() + "님이 회원님의 후기를 좋아합니다. ",
                         "https://i9b310.p.ssafy.io/drinkpost/" + drinkReview.getDrink().getDrinkId() + "/"
                         + reviewId);
    }
}