package com.ssafy.backend.domain.follow.dto;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.backend.domain.follow.entity.Follow;

class FollowResponseDtoTest {
    @Test
    void 팔로워와_팔로잉은_공개_프로필만_담는다() {
        Follow follow = Follow.builder().followId(1L).follower(emailNicknameUser(7L)).following(user(8L, "술꾼"))
                              .build();

        JsonNode json = toJson(FollowResponseDto.fromEntity(follow));

        assertPublicUser(json.get("follower"), 7L, "user_7");
        assertPublicUser(json.get("following"), 8L, "술꾼");
    }
}
