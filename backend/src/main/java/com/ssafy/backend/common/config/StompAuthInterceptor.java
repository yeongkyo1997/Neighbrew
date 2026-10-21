package com.ssafy.backend.common.config;

import java.security.Principal;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomUserManager;

import lombok.RequiredArgsConstructor;

// STOMP CONNECT 시 JWT를 검증해 세션 사용자를 설정하고, 구독/전송 대상에 대한 권한을 확인한다.
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    // 클라이언트가 보낼 수 있는 명령만 허용한다.
    // 서버 전용 프레임(MESSAGE 등)을 클라이언트가 보내면 SimpleBroker가 명령을 보지 않고 구독자에게 그대로 전달하므로 거부한다.
    private static final Set<StompCommand> CLIENT_COMMANDS = EnumSet.of(
            StompCommand.CONNECT, StompCommand.STOMP, StompCommand.SUBSCRIBE, StompCommand.UNSUBSCRIBE,
            StompCommand.SEND, StompCommand.ACK, StompCommand.NACK, StompCommand.BEGIN, StompCommand.COMMIT,
            StompCommand.ABORT, StompCommand.DISCONNECT);
    // SimpleBroker는 구독 목적지를 패턴(*, **)으로 해석하므로, 숫자 ID만 있는 정확한 형태만 허용한다.
    private static final Pattern DM_TOPIC = Pattern.compile("^/pub/dm/([0-9]+)/([0-9]+)$");
    private static final Pattern ROOM_TOPIC = Pattern.compile("^/pub/room/([0-9]+)$");
    private static final String APP_PREFIX = "/sub/";

    private final ChatRoomUserManager chatRoomUserManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (!CLIENT_COMMANDS.contains(command)) {
            throw new MessagingException("허용되지 않은 명령입니다");
        }

        // STOMP는 CONNECT의 별칭이므로 똑같이 토큰을 검증한다.
        if (StompCommand.CONNECT.equals(command) || StompCommand.STOMP.equals(command)) {
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
                throw new MessagingException("목적지가 없습니다");
            }
            // 전송은 애플리케이션 핸들러(/sub/...)로만 허용한다. (브로커 목적지 /pub 직접 전송 차단)
            if (StompCommand.SEND.equals(command)
                && (!destination.startsWith(APP_PREFIX) || destination.contains(".."))) {
                throw new MessagingException("허용되지 않은 목적지입니다");
            }
            if (StompCommand.SUBSCRIBE.equals(command)) {
                validateSubscription(Long.valueOf(user.getName()), destination);
            }
        }
        return message;
    }

    // 허용 목록에 없는 목적지는 모두 거부한다.
    private void validateSubscription(Long userId, String destination) {
        Matcher dm = DM_TOPIC.matcher(destination);
        if (dm.matches()) {
            String me = userId.toString();
            if (!dm.group(1).equals(me) && !dm.group(2).equals(me)) {
                throw new MessagingException("본인 DM만 구독할 수 있습니다");
            }
            return;
        }

        Matcher room = ROOM_TOPIC.matcher(destination);
        if (room.matches()) {
            Long roomId = Long.valueOf(room.group(1));
            if (!chatRoomUserManager.isRoomMember(roomId, userId)) {
                throw new MessagingException("참여중인 채팅방만 구독할 수 있습니다");
            }
            return;
        }

        throw new MessagingException("허용되지 않은 구독 목적지입니다");
    }
}
