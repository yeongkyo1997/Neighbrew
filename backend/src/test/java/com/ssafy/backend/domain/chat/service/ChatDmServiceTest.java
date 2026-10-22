package com.ssafy.backend.domain.chat.service;

import static com.ssafy.backend.support.UserFixture.OBJECT_MAPPER;
import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;
import static org.mockito.BDDMockito.given;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.chat.repository.ChatDmMessageRepository;
import com.ssafy.backend.domain.chat.repository.ChatDmRoomRepository;
import com.ssafy.backend.domain.chat.service.dto.ChatLeaveRequest;
import com.ssafy.backend.domain.chat.service.dto.ChatMessageRequest;
import com.ssafy.backend.domain.chat.service.manager.ChatDmMessageManager;
import com.ssafy.backend.domain.chat.service.manager.ChatDmRoomManager;
import com.ssafy.backend.domain.chat.service.validator.ChatDmValidator;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.domain.user.service.UserReader;

@ExtendWith(MockitoExtension.class)
class ChatDmServiceTest {
    @Mock
    private ChatDmMessageRepository chatDmMessageRepository;
    @Mock
    private ChatDmRoomRepository chatDmRoomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PushService pushService;
    @Mock
    private ChatDmValidator validator;
    @Mock
    private ChatDmRoomManager roomManager;
    @Mock
    private ChatDmMessageManager messageManager;

    private ChatDmService chatDmService;

    private final User me = emailNicknameUser(1L);
    private final User other = user(2L, "상대");

    @BeforeEach
    void setUp() {
        chatDmService = new ChatDmService(chatDmMessageRepository, chatDmRoomRepository, new UserReader(userRepository), pushService,
                                          validator, roomManager, messageManager, OBJECT_MAPPER);
    }

    private void givenUsers() {
        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(userRepository.findById(2L)).willReturn(Optional.of(other));
    }

    @Test
    void DM_조회의_두_사용자는_공개_프로필만_반환한다() {
        givenUsers();
        given(chatDmRoomRepository.findByUser1UserIdAndUser2UserId(1L, 2L)).willReturn(Optional.empty());

        JsonNode json = toJson(chatDmService.findDmMessagesByRoomId(1L, 2L, 1L));

        assertPublicUser(json.get("user1"), 1L, "user_1");
        assertPublicUser(json.get("user2"), 2L, "상대");
    }

    @Test
    void DM_전송_메시지의_보낸_사람은_공개_프로필만_담는다() throws Exception {
        givenUsers();
        given(validator.parseMessageRequest("payload")).willReturn(new ChatMessageRequest("안녕", 1L));
        given(roomManager.findOrCreateChatRoom(me, other)).willReturn(ChatDmRoom.builder().chatDmRoomId(1L).build());

        Map<String, Object> message = chatDmService.createChatOrSend(1L, 2L, "payload");

        assertPublicUser(toJson(message).get("user"), 1L, "user_1");
    }

    @Test
    void DM_퇴장_메시지의_사용자는_공개_프로필만_담는다() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(validator.parseLeaveRequest("payload")).willReturn(new ChatLeaveRequest(1L));

        String message = chatDmService.leaveDm(1L, 2L, "payload");

        assertPublicUser(OBJECT_MAPPER.readTree(message).get("user"), 1L, "user_1");
    }
}
