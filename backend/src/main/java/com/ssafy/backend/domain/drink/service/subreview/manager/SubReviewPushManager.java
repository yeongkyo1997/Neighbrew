package com.ssafy.backend.domain.drink.service.subreview.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubReviewPushManager {
    private final PushService pushService;

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

    public void sendSubReviewNotification(User commenter, DrinkReview drinkReview) {
        pushService.send(commenter, drinkReview.getUser(), PushType.SUBREVIEW,
                         commenter.getNickname() + "님이 회원님의 후기에 댓글을 남겼습니다.",
                         neighbrewUrl + "/drinkpost/" + drinkReview.getDrink().getDrinkId() + "/"
                         + drinkReview.getDrinkReviewId());
    }
}