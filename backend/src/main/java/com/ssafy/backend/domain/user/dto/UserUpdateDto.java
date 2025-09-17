package com.ssafy.backend.domain.user.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDto {
    String email;
    String nickname;
    String name;
    LocalDate birth;
    String intro;
    Float liverPoint;
    String profile;
}
