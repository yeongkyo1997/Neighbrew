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
}
