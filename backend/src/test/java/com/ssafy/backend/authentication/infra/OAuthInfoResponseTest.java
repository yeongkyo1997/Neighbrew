package com.ssafy.backend.authentication.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.authentication.infra.kakao.KakaoInfoResponse;
import com.ssafy.backend.authentication.infra.naver.NaverInfoResponse;

class OAuthInfoResponseTest {
    // RestTemplate의 JSON 메시지 컨버터가 쓰는 기본 ObjectMapper
    private final ObjectMapper objectMapper = new MappingJackson2HttpMessageConverter().getObjectMapper();

    @Test
    void 이메일_동의가_없는_카카오_응답에서_고유_ID를_읽는다() throws Exception {
        String json = "{\"id\":3012345678,\"kakao_account\":{\"profile\":{\"nickname\":\"홍길동\"}}}";

        KakaoInfoResponse response = objectMapper.readValue(json, KakaoInfoResponse.class);

        assertThat(response.getOAuthId()).isEqualTo("3012345678");
        assertThat(response.getEmail()).isNull();
        assertThat(response.getName()).isEqualTo("홍길동");
    }

    @Test
    void 이메일_동의가_없는_네이버_응답에서_고유_ID를_읽는다() throws Exception {
        String json = "{\"resultcode\":\"00\",\"message\":\"success\","
                      + "\"response\":{\"id\":\"naver-unique-id\",\"nickname\":\"홍길동\",\"name\":\"홍길동\"}}";

        NaverInfoResponse response = objectMapper.readValue(json, NaverInfoResponse.class);

        assertThat(response.getOAuthId()).isEqualTo("naver-unique-id");
        assertThat(response.getEmail()).isNull();
        assertThat(response.getName()).isEqualTo("홍길동");
    }

    @Test
    void 카카오_응답에_프로필이나_계정_정보가_없어도_NPE_없이_null을_돌려준다() throws Exception {
        KakaoInfoResponse noProfile = objectMapper.readValue("{\"id\":1,\"kakao_account\":{}}", KakaoInfoResponse.class);
        KakaoInfoResponse noAccount = objectMapper.readValue("{\"id\":1}", KakaoInfoResponse.class);

        assertThat(noProfile.getName()).isNull();
        assertThat(noProfile.getEmail()).isNull();
        assertThat(noAccount.getName()).isNull();
        assertThat(noAccount.getEmail()).isNull();
        assertThat(noAccount.getOAuthId()).isEqualTo("1");
    }

    @Test
    void 네이버_실패_응답에는_response가_없어도_NPE_없이_null을_돌려준다() throws Exception {
        NaverInfoResponse response = objectMapper.readValue("{\"resultcode\":\"024\",\"message\":\"Authentication failed\"}",
                                                            NaverInfoResponse.class);

        assertThat(response.getOAuthId()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getName()).isNull();
    }
}
