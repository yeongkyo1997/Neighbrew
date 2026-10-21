package com.ssafy.backend.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.authentication.domain.oauth.RequestOAuthInfoService;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {
    private static final String RANDOM_NICKNAME = "user_[0-9a-f]{8}";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestOAuthInfoService requestOAuthInfoService;
    @Mock
    private OAuthLoginParams params;
    @Mock
    private OAuthInfoResponse oAuthInfo;

    @InjectMocks
    private OAuthLoginService oAuthLoginService;

    private void givenNewUser(String email) throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getEmail()).willReturn(email);
        given(oAuthInfo.getName()).willReturn("홍길동");
        given(oAuthInfo.getOAuthProvider()).willReturn(OAuthProvider.KAKAO);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
    }

    private User savedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        return captor.getValue();
    }

    @Test
    void 신규_가입자_닉네임은_이메일이_아닌_무작위_값이다() throws Exception {
        givenNewUser("new@example.com");
        given(userRepository.existsByNickname(anyString())).willReturn(false);

        oAuthLoginService.login(params);

        User saved = savedUser();
        assertThat(saved.getNickname()).matches(RANDOM_NICKNAME);
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void 이메일이_없는_신규_가입자도_이름_대신_무작위_닉네임을_받는다() throws Exception {
        givenNewUser(null);
        given(userRepository.existsByNickname(anyString())).willReturn(false);

        oAuthLoginService.login(params);

        assertThat(savedUser().getNickname()).matches(RANDOM_NICKNAME);
    }

    @Test
    void 무작위_닉네임이_이미_있으면_다시_뽑는다() throws Exception {
        givenNewUser("new@example.com");
        given(userRepository.existsByNickname(anyString())).willReturn(true, false);

        oAuthLoginService.login(params);

        then(userRepository).should(times(2)).existsByNickname(anyString());
        assertThat(savedUser().getNickname()).matches(RANDOM_NICKNAME);
    }

    @Test
    void 기존_사용자는_새로_저장하지_않는다() throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getEmail()).willReturn("old@example.com");
        given(userRepository.findByEmail("old@example.com")).willReturn(
                Optional.of(User.builder().userId(3L).build()));

        LoginResponse response = oAuthLoginService.login(params);

        assertThat(response.getUserId()).isEqualTo(3L);
        then(userRepository).should(never()).save(any());
    }
}
