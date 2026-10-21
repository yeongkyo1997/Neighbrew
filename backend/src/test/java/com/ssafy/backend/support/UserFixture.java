package com.ssafy.backend.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.domain.user.entity.User;

// 개인정보(email·name·birth 등)를 모두 채운 사용자와, 응답 JSON에 공개 프로필만 남았는지 확인하는 헬퍼
public final class UserFixture {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final Set<String> PUBLIC_FIELDS = Set.of("userId", "nickname", "profile", "intro", "liverPoint");

    private UserFixture() {
    }

    public static User user(Long userId, String nickname) {
        return User.builder().userId(userId).email("user" + userId + "@example.com").nickname(nickname)
                   .oAuthProvider(OAuthProvider.KAKAO).name("홍길동").birth(LocalDate.of(1990, 1, 1)).intro("안녕하세요")
                   .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0)).updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0))
                   .liverPoint(42.5f).profile("profile.png").build();
    }

    // 닉네임이 이메일인 예전 가입자
    public static User emailNicknameUser(Long userId) {
        return user(userId, "user" + userId + "@example.com");
    }

    public static void assertPublicUser(JsonNode node, Long userId, String nickname) {
        Set<String> fields = new HashSet<>();
        node.fieldNames().forEachRemaining(fields::add);
        assertThat(fields).isEqualTo(PUBLIC_FIELDS);
        assertThat(node.get("userId").asLong()).isEqualTo(userId);
        assertThat(node.get("nickname").asText()).isEqualTo(nickname);
    }

    public static JsonNode toJson(Object value) {
        return OBJECT_MAPPER.valueToTree(value);
    }
}
