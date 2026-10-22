package com.ssafy.backend.domain.user.dto;

import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserResponseDto {
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder().userId(user.getUserId()).email(user.getEmail()).nickname(
                user.getNickname()).name(user.getName()).birth(user.getBirth() == null ? null : user.getBirth().toString()).intro(
                user.getIntro()).liverPoint(user.getLiverPoint()).profile(user.getProfile()).build();
    }

    private Long userId;
    private String email;
    private String nickname;
    private String name;
    private String birth;
    private String intro;
    private Float liverPoint;
    private String profile;
}
