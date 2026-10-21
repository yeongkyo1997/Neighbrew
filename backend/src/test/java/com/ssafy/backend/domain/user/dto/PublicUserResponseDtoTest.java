package com.ssafy.backend.domain.user.dto;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PublicUserResponseDtoTest {
    @Test
    void 공개_프로필_필드만_담는다() {
        PublicUserResponseDto dto = PublicUserResponseDto.fromEntity(user(7L, "술꾼"));

        assertThat(dto.getUserId()).isEqualTo(7L);
        assertThat(dto.getNickname()).isEqualTo("술꾼");
        assertThat(dto.getProfile()).isEqualTo("profile.png");
        assertThat(dto.getIntro()).isEqualTo("안녕하세요");
        assertThat(dto.getLiverPoint()).isEqualTo(42.5f);
        assertPublicUser(toJson(dto), 7L, "술꾼");
    }

    @Test
    void 이메일_닉네임은_user_아이디로_가린다() {
        PublicUserResponseDto dto = PublicUserResponseDto.fromEntity(emailNicknameUser(7L));

        assertThat(dto.getNickname()).isEqualTo("user_7");
    }
}
