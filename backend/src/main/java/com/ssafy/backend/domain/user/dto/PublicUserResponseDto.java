package com.ssafy.backend.domain.user.dto;

import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 다른 사용자에게 보여주는 공개 프로필. email·name·birth는 본인 조회(UserResponseDto)에서만 내려준다.
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PublicUserResponseDto {
    public static PublicUserResponseDto fromEntity(User user) {
        return PublicUserResponseDto.builder().userId(user.getUserId()).nickname(publicNickname(user)).profile(
                user.getProfile()).intro(user.getIntro()).liverPoint(user.getLiverPoint()).build();
    }

    // 예전 가입자는 이메일이 닉네임으로 저장돼 있으므로 '@'가 든 닉네임은 공개하지 않는다.
    private static String publicNickname(User user) {
        String nickname = user.getNickname();
        return nickname != null && nickname.contains("@") ? "user_" + user.getUserId() : nickname;
    }

    private Long userId;
    private String nickname;
    private String profile;
    private String intro;
    private Float liverPoint;
}
