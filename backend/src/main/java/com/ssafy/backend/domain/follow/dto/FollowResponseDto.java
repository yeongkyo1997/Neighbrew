package com.ssafy.backend.domain.follow.dto;

import com.ssafy.backend.domain.follow.entity.Follow;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FollowResponseDto {
    public static FollowResponseDto fromEntity(Follow follow) {
        return FollowResponseDto.builder().follower(follow.getFollower()).following(follow.getFollowing())
                                .build();
    }

    private User follower;
    private User following;
}
