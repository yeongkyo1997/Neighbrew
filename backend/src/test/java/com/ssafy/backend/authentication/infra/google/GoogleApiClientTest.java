package com.ssafy.backend.authentication.infra.google;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.infra.google.GoogleInfoResponse.Response;

@ExtendWith(MockitoExtension.class)
class GoogleApiClientTest {
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    // RestTemplate의 JSON 메시지 컨버터가 쓰는 기본 ObjectMapper
    private final ObjectMapper objectMapper = new MappingJackson2HttpMessageConverter().getObjectMapper();

    @Mock
    private RestTemplate restTemplate;

    private GoogleApiClient client;

    @BeforeEach
    void setUp() {
        client = new GoogleApiClient(restTemplate);
        ReflectionTestUtils.setField(client, "userInfoUrl", USER_INFO_URL);
    }

    private Response response(String json) throws Exception {
        return objectMapper.readValue(json, Response.class);
    }

    @Test
    void 사용자_정보는_토큰을_URL이_아니라_Authorization_헤더로_보낸다() throws Exception {
        given(restTemplate.exchange(eq(USER_INFO_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Response.class)))
                .willReturn(ResponseEntity.ok(response("{\"id\":\"1\"}")));

        client.requestOauthInfo("secret-token");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<?>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        then(restTemplate).should().exchange(eq(USER_INFO_URL), eq(HttpMethod.GET), captor.capture(), eq(Response.class));
        assertThat(captor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer secret-token");
    }

    @Test
    void 구글_사용자_정보에서_고유_ID와_이메일_인증_여부를_읽는다() throws Exception {
        Response body = response("{\"id\":\"google-unique-id\",\"email\":\"a@gmail.com\",\"verified_email\":true,"
                                 + "\"name\":\"홍길동\"}");
        given(restTemplate.exchange(eq(USER_INFO_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Response.class)))
                .willReturn(ResponseEntity.ok(body));

        OAuthInfoResponse info = client.requestOauthInfo("token");

        assertThat(info.getOAuthId()).isEqualTo("google-unique-id");
        assertThat(info.getEmail()).isEqualTo("a@gmail.com");
        assertThat(info.getName()).isEqualTo("홍길동");
        assertThat(body.verifiedEmail).isTrue();
    }

    @Test
    void 응답_객체가_없어도_NPE_없이_null을_돌려준다() {
        GoogleInfoResponse info = new GoogleInfoResponse(null);

        assertThat(info.getOAuthId()).isNull();
        assertThat(info.getEmail()).isNull();
        assertThat(info.getName()).isNull();
    }
}
