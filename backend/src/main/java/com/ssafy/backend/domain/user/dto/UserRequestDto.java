package com.ssafy.backend.domain.user.dto;

import java.time.LocalDate;

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
public class UserRequestDto {
    private Long userId;

    private String email;

    private String nickname;

    private String name;

    private String birth;

    private String intro;

    private Float liverPoint;

    private String profile;

    public User toEntity() {
        return User.builder().email(email).nickname(nickname).name(name).birth(LocalDate.parse(birth)).intro(
                intro).liverPoint(liverPoint).profile(profile).build();
    }
}
