package com.ssafy.backend.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        given(oAuthInfo.getOAuthId()).willReturn("3012345678");
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

    @Test
    void 이메일이_없으면_provider와_고유_ID로_가입한다() throws Exception {
        givenNewUser(null);
        given(oAuthInfo.getOAuthId()).willReturn("3012345678");
        given(userRepository.existsByNickname(anyString())).willReturn(false);

        oAuthLoginService.login(params);

        then(userRepository).should().findByEmail("kakao_3012345678");
        User saved = savedUser();
        assertThat(saved.getEmail()).isEqualTo("kakao_3012345678");
        assertThat(saved.getName()).isEqualTo("홍길동");
    }

    @Test
    void 이메일이_없는_사용자가_다시_로그인하면_기존_계정을_찾는다() throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getEmail()).willReturn(null);
        given(oAuthInfo.getOAuthId()).willReturn("3012345678");
        given(oAuthInfo.getOAuthProvider()).willReturn(OAuthProvider.KAKAO);
        given(userRepository.findByEmail("kakao_3012345678")).willReturn(
                Optional.of(User.builder().userId(1L).build()));

        LoginResponse response = oAuthLoginService.login(params);

        assertThat(response.getUserId()).isEqualTo(1L);
        then(userRepository).should(never()).save(any());
    }

    @Test
    void 이메일이_없으면_이메일처럼_생긴_표시_이름이라도_계정_조회에_쓰지_않는다() throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getEmail()).willReturn(null);
        given(oAuthInfo.getOAuthId()).willReturn("3012345678");
        given(oAuthInfo.getName()).willReturn("victim@gmail.com");
        given(oAuthInfo.getOAuthProvider()).willReturn(OAuthProvider.KAKAO);
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        oAuthLoginService.login(params);

        then(userRepository).should(never()).findByEmail("victim@gmail.com");
        assertThat(savedUser().getEmail()).isEqualTo("kakao_3012345678");
    }

    @Test
    void 이메일과_고유_ID가_모두_없으면_가입하지_않는다() throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getEmail()).willReturn(null);
        given(oAuthInfo.getOAuthId()).willReturn(null);

        assertThatThrownBy(() -> oAuthLoginService.login(params)).isInstanceOf(IllegalArgumentException.class);
        then(userRepository).should(never()).save(any());
    }
}
