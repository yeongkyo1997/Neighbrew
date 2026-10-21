package com.ssafy.backend.common.config;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.util.JwtUtil;

// STOMP CONNECT 시 JWT를 검증해 세션 사용자를 설정하고, 구독/전송 대상에 대한 권한을 확인한다.
@Component
public class StompAuthInterceptor implements ChannelInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String DM_TOPIC = "/pub/dm/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith(BEARER_PREFIX)) {
                throw new MessagingException("토큰 형식이 잘못됐습니다");
            }
            String userId;
            try {
                userId = JwtUtil.getClaims(header.substring(BEARER_PREFIX.length())).getSubject();
            } catch (Exception e) {
                throw new MessagingException("잘못된 토큰입니다");
            }
            final String loginUserId = userId;
            Principal principal = () -> loginUserId;
            accessor.setUser(principal);
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
            Principal user = accessor.getUser();
            if (user == null) {
                throw new MessagingException("인증되지 않은 연결입니다");
            }
            String destination = accessor.getDestination();
            if (destination == null) {
                return message;
            }
            // 클라이언트가 브로커 목적지(/pub)로 직접 메시지를 보내는 것은 막는다.
            if (StompCommand.SEND.equals(command) && destination.startsWith("/pub")) {
                throw new MessagingException("허용되지 않은 목적지입니다");
            }
            if (StompCommand.SUBSCRIBE.equals(command)) {
                validateSubscription(Long.valueOf(user.getName()), destination);
            }
        }
        return message;
    }

    private void validateSubscription(Long userId, String destination) {
        if (destination.startsWith(DM_TOPIC)) {
            String[] ids = destination.substring(DM_TOPIC.length()).split("/");
            if (ids.length < 2 || !(ids[0].equals(userId.toString()) || ids[1].equals(userId.toString()))) {
                throw new MessagingException("본인 DM만 구독할 수 있습니다");
            }
        }
    }
}
