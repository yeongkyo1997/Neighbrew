package com.ssafy.backend.domain.user.service;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.storage.service.s3.S3Service;
import com.ssafy.backend.domain.user.dto.UserResponseDto;
import com.ssafy.backend.domain.user.dto.UserUpdateDto;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserService userService;

    private UserUpdateDto updateDto(String nickname, String intro) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setNickname(nickname);
        dto.setIntro(intro);
        dto.setBirth(LocalDate.of(1990, 1, 1));
        return dto;
    }

    @Test
    void 다른_사용자_조회는_공개_프로필만_반환한다() {
        given(userRepository.findById(7L)).willReturn(Optional.of(emailNicknameUser(7L)));

        assertPublicUser(toJson(userService.findByUserId(7L)), 7L, "user_7");
    }

    @Test
    void 전체_목록은_공개_프로필만_반환한다() {
        given(userRepository.findAll()).willReturn(List.of(emailNicknameUser(7L)));

        assertPublicUser(toJson(userService.findAll()).get(0), 7L, "user_7");
    }

    @Test
    void 닉네임_검색은_이메일_닉네임을_매칭하지_않고_공개_프로필만_반환한다() {
        // 가려진 이메일 닉네임까지 매칭하면 검색어를 한 글자씩 늘려 이메일을 알아낼 수 있다.
        given(userRepository.findByNicknameContainingAndNicknameNotContaining("술", "@")).willReturn(
                List.of(user(8L, "술꾼")));

        assertPublicUser(toJson(userService.searchUsers("술")).get(0), 8L, "술꾼");
    }

    @Test
    void 내_정보_조회는_이메일_이름_생년월일을_포함한다() {
        given(userRepository.findById(7L)).willReturn(Optional.of(emailNicknameUser(7L)));

        UserResponseDto myInfo = userService.findMyInfo(7L);

        assertThat(myInfo.getEmail()).isEqualTo("user7@example.com");
        assertThat(myInfo.getName()).isEqualTo("홍길동");
        assertThat(myInfo.getBirth()).isEqualTo("1990-01-01");
        assertThat(myInfo.getNickname()).isEqualTo("user7@example.com");
    }

    @Test
    void 닉네임을_이메일_형태로_바꿀_수_없다() {
        given(userRepository.findById(7L)).willReturn(Optional.of(user(7L, "술꾼")));

        assertThatThrownBy(() -> userService.updateUser(7L, updateDto("new@example.com", "안녕하세요")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("@");
        then(userRepository).should(never()).save(any());
    }

    @ParameterizedTest
    // 대소문자, 앞뒤 공백, 전각 숫자, 제로폭 공백, 다른 문자 체계의 숫자로 우회해도 막는다.
    @ValueSource(strings = { "user_7", "USER_7", " user_7 ", "user_７", "user_​7", "user_٧" })
    void 가려진_공개_이름과_같은_형식의_닉네임으로_바꿀_수_없다(String nickname) {
        given(userRepository.findById(9L)).willReturn(Optional.of(user(9L, "술꾼")));

        assertThatThrownBy(() -> userService.updateUser(9L, updateDto(nickname, "안녕하세요")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("user_");
        then(userRepository).should(never()).save(any());
    }

    @Test
    void 전각_골뱅이도_닉네임에_쓸_수_없다() {
        given(userRepository.findById(9L)).willReturn(Optional.of(user(9L, "술꾼")));

        assertThatThrownBy(() -> userService.updateUser(9L, updateDto("new＠example.com", "안녕하세요")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("@");
    }

    @Test
    void 이메일_닉네임인_기존_사용자도_닉네임은_두고_소개를_바꿀_수_있다() {
        User user = emailNicknameUser(7L);
        given(userRepository.findById(7L)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("user7@example.com")).willReturn(true);
        given(userRepository.save(user)).willReturn(user);

        UserResponseDto result = userService.updateUser(7L, updateDto("user7@example.com", "새 소개"));

        assertThat(result.getIntro()).isEqualTo("새 소개");
    }
}
