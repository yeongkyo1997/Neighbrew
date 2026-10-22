package com.ssafy.backend.authentication.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.infra.kakao.KakaoApiClient;
import com.ssafy.backend.authentication.infra.kakao.KakaoInfoResponse;
import com.ssafy.backend.authentication.infra.kakao.KakaoTokens;
import com.ssafy.backend.exception.InvalidOAuthCodeException;
import com.ssafy.backend.exception.OAuthProviderException;

// 공통 흐름(AbstractOAuthApiClient)을 카카오 구현으로 확인한다
@ExtendWith(MockitoExtension.class)
class AbstractOAuthApiClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OAuthLoginParams params;

    private KakaoApiClient client;

    @BeforeEach
    void setUp() {
        client = new KakaoApiClient(restTemplate);
        ReflectionTestUtils.setField(client, "authUrl", "https://kauth.kakao.com");
        ReflectionTestUtils.setField(client, "apiUrl", "https://kapi.kakao.com");
        ReflectionTestUtils.setField(client, "clientId", "client-id");
    }

    private void givenTokenResponse(KakaoTokens tokens) {
        given(params.makeBody()).willReturn(new LinkedMultiValueMap<>());
        given(restTemplate.postForObject(eq("https://kauth.kakao.com/oauth/token"), any(), eq(KakaoTokens.class)))
                .willReturn(tokens);
    }

    private KakaoTokens tokens(String accessToken) {
        KakaoTokens tokens = new KakaoTokens();
        ReflectionTestUtils.setField(tokens, "accessToken", accessToken);
        return tokens;
    }

    @Test
    void 토큰_응답에서_액세스_토큰을_꺼낸다() {
        givenTokenResponse(tokens("access-token"));

        assertThat(client.requestAccessToken(params)).isEqualTo("access-token");
    }

    @Test
    void 토큰_응답_본문이_비면_provider_오류로_처리한다() {
        givenTokenResponse(null);

        assertThatThrownBy(() -> client.requestAccessToken(params)).isInstanceOf(OAuthProviderException.class);
    }

    @Test
    void 액세스_토큰이_비어_있으면_provider_오류로_처리한다() {
        givenTokenResponse(tokens(" "));

        assertThatThrownBy(() -> client.requestAccessToken(params)).isInstanceOf(OAuthProviderException.class);
    }

    @Test
    void 인가_코드가_거절되면_인증_오류로_처리한다() {
        given(params.makeBody()).willReturn(new LinkedMultiValueMap<>());
        given(restTemplate.postForObject(anyString(), any(), eq(KakaoTokens.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.requestAccessToken(params)).isInstanceOf(InvalidOAuthCodeException.class);
    }

    @Test
    void provider_서버_오류나_네트워크_오류는_provider_오류로_처리한다() {
        given(params.makeBody()).willReturn(new LinkedMultiValueMap<>());
        given(restTemplate.postForObject(anyString(), any(), eq(KakaoTokens.class)))
                .willThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR),
                           new ResourceAccessException("timeout"));

        assertThatThrownBy(() -> client.requestAccessToken(params)).isInstanceOf(OAuthProviderException.class);
        assertThatThrownBy(() -> client.requestAccessToken(params)).isInstanceOf(OAuthProviderException.class);
    }

    @Test
    void 사용자_정보_응답이_비면_provider_오류로_처리한다() {
        given(restTemplate.postForObject(eq("https://kapi.kakao.com/v2/user/me"), any(), eq(KakaoInfoResponse.class)))
                .willReturn(null);

        assertThatThrownBy(() -> client.requestOauthInfo("access-token")).isInstanceOf(OAuthProviderException.class);
    }

    @Test
    void 사용자_정보_요청이_실패하면_provider_오류로_처리한다() {
        given(restTemplate.postForObject(anyString(), any(), eq(KakaoInfoResponse.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.requestOauthInfo("access-token")).isInstanceOf(OAuthProviderException.class);
    }

    @Test
    void 사용자_정보를_그대로_돌려준다() {
        KakaoInfoResponse info = mock(KakaoInfoResponse.class);
        given(restTemplate.postForObject(anyString(), any(), eq(KakaoInfoResponse.class))).willReturn(info);

        assertThat(client.requestOauthInfo("access-token")).isSameAs(info);
    }
}
