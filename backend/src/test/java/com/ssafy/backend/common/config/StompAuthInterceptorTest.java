package com.ssafy.backend.common.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomUserManager;

@ExtendWith(MockitoExtension.class)
class StompAuthInterceptorTest {
    @Mock
    private ChatRoomUserManager chatRoomUserManager;

    @InjectMocks
    private StompAuthInterceptor interceptor;

    private Message<?> frame(StompCommand command, Consumer<StompHeaderAccessor> customizer) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        customizer.accept(accessor);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Message<?> authenticated(StompCommand command, String destination) {
        return frame(command, a -> {
            a.setUser(() -> "2");
            a.setDestination(destination);
        });
    }

    private String userOf(Message<?> message) {
        return StompHeaderAccessor.wrap(message).getUser().getName();
    }

    @Test
    void 클라이언트가_보낸_MESSAGE_프레임은_거부한다() {
        Message<?> message = authenticated(StompCommand.MESSAGE, "/pub/room/1");

        assertThatThrownBy(() -> interceptor.preSend(message, null)).isInstanceOf(MessagingException.class);
    }

    @Test
    void 토큰_없는_STOMP_프레임은_거부한다() {
        Message<?> message = frame(StompCommand.STOMP, a -> {
        });

        assertThatThrownBy(() -> interceptor.preSend(message, null)).isInstanceOf(MessagingException.class);
    }

    @Test
    void 유효한_토큰의_STOMP와_CONNECT는_사용자를_설정한다() {
        String header = "Bearer " + JwtUtil.generateToken("2");

        for (StompCommand command : new StompCommand[] {StompCommand.STOMP, StompCommand.CONNECT}) {
            Message<?> message = frame(command, a -> a.addNativeHeader("Authorization", header));
            assertThat(userOf(interceptor.preSend(message, null))).isEqualTo("2");
        }
    }

    @Test
    void 브로커_목적지로의_SEND는_거부한다() {
        Message<?> message = authenticated(StompCommand.SEND, "/pub/room/1");

        assertThatThrownBy(() -> interceptor.preSend(message, null)).isInstanceOf(MessagingException.class);
    }

    @Test
    void 참여하지_않은_채팅방_구독은_거부한다() {
        given(chatRoomUserManager.isRoomMember(1L, 2L)).willReturn(false);
        Message<?> message = authenticated(StompCommand.SUBSCRIBE, "/pub/room/1");

        assertThatThrownBy(() -> interceptor.preSend(message, null)).isInstanceOf(MessagingException.class);
    }

    @Test
    void 참여중인_채팅방_구독은_허용한다() {
        given(chatRoomUserManager.isRoomMember(1L, 2L)).willReturn(true);
        Message<?> message = authenticated(StompCommand.SUBSCRIBE, "/pub/room/1");

        assertThat(interceptor.preSend(message, null)).isSameAs(message);
    }

    @Test
    void 인증_전_DISCONNECT는_통과시킨다() {
        Message<?> message = frame(StompCommand.DISCONNECT, a -> {
        });

        assertThat(interceptor.preSend(message, null)).isSameAs(message);
    }
}
