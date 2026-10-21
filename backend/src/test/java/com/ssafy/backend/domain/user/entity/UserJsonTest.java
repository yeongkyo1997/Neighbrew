package com.ssafy.backend.domain.user.entity;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.chat.entity.ChatDmMessage;
import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;

// User 엔티티를 그대로 반환하는 응답(Push, Meet 생성, DM, 채팅방 유저 목록)에서도 공개 프로필만 나가는지 확인한다.
class UserJsonTest {
    // Hibernate 지연 로딩 프록시처럼 필드는 비어 있고 메서드 호출만 실제 엔티티에 위임하는 하위 클래스
    static class ProxyLikeUser extends User {
        private final User target;

        ProxyLikeUser(User target) {
            this.target = target;
        }

        @Override
        public String getEmail() {
            return target.getEmail();
        }

        @Override
        public PublicUserResponseDto toPublicResponse() {
            return target.toPublicResponse();
        }
    }

    @Test
    void User_엔티티는_공개_프로필로_직렬화된다() {
        assertPublicUser(toJson(user(7L, "술꾼")), 7L, "술꾼");
        assertPublicUser(toJson(emailNicknameUser(8L)), 8L, "user_8");
    }

    @Test
    void 지연_로딩_프록시도_공개_프로필로_직렬화된다() {
        assertPublicUser(toJson(new ProxyLikeUser(emailNicknameUser(7L))), 7L, "user_7");
    }

    @Test
    void Map에_담긴_User_목록도_공개_프로필로_직렬화된다() {
        JsonNode json = toJson(Map.of("users", List.of(emailNicknameUser(7L))));

        assertPublicUser(json.get("users").get(0), 7L, "user_7");
    }

    @Test
    void 알림의_보낸_사람과_받는_사람은_공개_프로필로_직렬화된다() {
        Push push = Push.builder().pushId(1L).pushType(PushType.DM).content("메시지").sender(emailNicknameUser(7L))
                        .receiver(user(8L, "받는사람")).build();

        JsonNode json = toJson(push);

        assertPublicUser(json.get("sender"), 7L, "user_7");
        assertPublicUser(json.get("receiver"), 8L, "받는사람");
    }

    @Test
    void DM_메시지의_작성자와_방_참여자는_공개_프로필로_직렬화된다() {
        ChatDmRoom room = ChatDmRoom.builder().chatDmRoomId(1L).user1(emailNicknameUser(7L)).user2(user(8L, "상대"))
                                    .build();
        ChatDmMessage message = ChatDmMessage.builder().chatDmMessageId(1L).chatDmRoom(room)
                                             .user(emailNicknameUser(7L)).message("안녕").build();

        JsonNode json = toJson(message);

        assertPublicUser(json.get("user"), 7L, "user_7");
        assertPublicUser(json.get("chatDmRoom").get("user1"), 7L, "user_7");
        assertPublicUser(json.get("chatDmRoom").get("user2"), 8L, "상대");
    }

    @Test
    void 모임_방장은_공개_프로필로_직렬화된다() {
        Meet meet = Meet.builder().meetId(1L).meetName("모임").host(emailNicknameUser(7L)).build();

        assertPublicUser(toJson(meet).get("host"), 7L, "user_7");
    }
}
