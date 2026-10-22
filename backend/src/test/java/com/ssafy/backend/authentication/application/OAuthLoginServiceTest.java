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

    private void givenOAuthInfo(OAuthProvider provider, String oAuthId, String email) throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getOAuthProvider()).willReturn(provider);
        given(oAuthInfo.getOAuthId()).willReturn(oAuthId);
        given(oAuthInfo.getEmail()).willReturn(email);
    }

    private void givenNewUser(String email) throws Exception {
        givenOAuthInfo(OAuthProvider.KAKAO, "3012345678", email);
        given(oAuthInfo.getName()).willReturn("홍길동");
        given(userRepository.findByOAuth(OAuthProvider.KAKAO, "3012345678")).willReturn(Optional.empty());
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
    }

    private User savedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        return captor.getValue();
    }

    @Test
    void 신규_가입자는_provider와_고유_ID로_저장한다() throws Exception {
        givenNewUser("new@example.com");

        oAuthLoginService.login(params);

        User saved = savedUser();
        assertThat(saved.getOAuthProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(saved.getOAuthId()).isEqualTo("3012345678");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getName()).isEqualTo("홍길동");
    }

    @Test
    void 신규_가입자_닉네임은_이메일이_아닌_무작위_값이다() throws Exception {
        givenNewUser("new@example.com");

        oAuthLoginService.login(params);

        assertThat(savedUser().getNickname()).matches(RANDOM_NICKNAME);
    }

    @Test
    void 이메일이_없는_신규_가입자도_이름_대신_무작위_닉네임을_받는다() throws Exception {
        givenNewUser(null);

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
    void 이메일이_없으면_이메일을_비워서_저장한다() throws Exception {
        givenNewUser(null);

        oAuthLoginService.login(params);

        User saved = savedUser();
        assertThat(saved.getEmail()).isNull();
        assertThat(saved.getOAuthId()).isEqualTo("3012345678");
    }

    @Test
    void 기존_사용자는_새로_저장하지_않는다() throws Exception {
        givenOAuthInfo(OAuthProvider.KAKAO, "3012345678", "old@example.com");
        given(userRepository.findByOAuth(OAuthProvider.KAKAO, "3012345678")).willReturn(
                Optional.of(User.builder().userId(3L).build()));

        LoginResponse response = oAuthLoginService.login(params);

        assertThat(response.getUserId()).isEqualTo(3L);
        then(userRepository).should(never()).save(any());
    }

    @Test
    void 이메일_동의가_바뀌어도_고유_ID가_같으면_같은_계정을_찾는다() throws Exception {
        given(userRepository.findByOAuth(OAuthProvider.KAKAO, "3012345678")).willReturn(
                Optional.of(User.builder().userId(1L).build()));

        // 이메일 없이 로그인
        givenOAuthInfo(OAuthProvider.KAKAO, "3012345678", null);
        LoginResponse withoutEmail = oAuthLoginService.login(params);
        // 나중에 이메일 제공에 동의한 뒤 로그인
        given(oAuthInfo.getEmail()).willReturn("new@example.com");
        LoginResponse withEmail = oAuthLoginService.login(params);

        assertThat(withoutEmail.getUserId()).isEqualTo(1L);
        assertThat(withEmail.getUserId()).isEqualTo(1L);
        then(userRepository).should(never()).save(any());
    }

    @Test
    void 다른_provider에서_같은_이메일로_로그인해도_기존_계정을_찾지_않는다() throws Exception {
        // victim@gmail.com은 GOOGLE 계정의 이메일이지만, KAKAO 고유 ID로만 찾으므로 그 계정에 닿지 않는다
        givenOAuthInfo(OAuthProvider.KAKAO, "attacker-id", "victim@gmail.com");
        given(oAuthInfo.getName()).willReturn("공격자");
        given(userRepository.findByOAuth(OAuthProvider.KAKAO, "attacker-id")).willReturn(Optional.empty());
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        oAuthLoginService.login(params);

        then(userRepository).should().findByOAuth(OAuthProvider.KAKAO, "attacker-id");
        User saved = savedUser();
        assertThat(saved.getOAuthProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(saved.getOAuthId()).isEqualTo("attacker-id");
    }

    @Test
    void 고유_ID가_없으면_가입하지_않는다() throws Exception {
        given(requestOAuthInfoService.request(params)).willReturn(oAuthInfo);
        given(oAuthInfo.getOAuthId()).willReturn(null);

        assertThatThrownBy(() -> oAuthLoginService.login(params)).isInstanceOf(IllegalArgumentException.class);
        then(userRepository).should(never()).save(any());
    }
}
