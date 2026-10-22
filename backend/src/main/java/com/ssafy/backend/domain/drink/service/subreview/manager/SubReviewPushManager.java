package com.ssafy.backend.domain.drink.service.subreview.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.push.service.push.PushMessage;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubReviewPushManager {
    private final PushService pushService;

    public void sendSubReviewNotification(User commenter, DrinkReview drinkReview) {
        pushService.send(commenter, drinkReview.getUser(),
                         PushMessage.subReview(commenter.getNickname(), drinkReview.getDrink().getDrinkId(),
                                               drinkReview.getDrinkReviewId()));
    }
}