package com.ssafy.backend.domain.follow.dto;

import com.ssafy.backend.domain.follow.entity.Follow;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FollowRequestDto {
    private Long followingId;
    private Long followerId;

    public Follow toEntity(User following, User follower) {
        return Follow.builder().following(following).follower(follower).build();
    }
}
