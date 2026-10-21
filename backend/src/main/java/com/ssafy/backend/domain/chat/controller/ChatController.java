package com.ssafy.backend.domain.chat.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.backend.domain.chat.service.ChatDmService;
import com.ssafy.backend.domain.chat.service.room.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatDmService chatDmService;
    private final ObjectMapper mapper = new ObjectMapper();

    //단체 메세지를 보낸다.
    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendChatRoomMessage(@DestinationVariable Long roomId, @Payload String data, Principal principal)
            throws JsonProcessingException {
        String res = chatRoomService.sendChatRoomMessage(roomId, withLoginUser(data, "userId", principal));
        messagingTemplate.convertAndSend("/pub/room/" + roomId, res);
    }

    // 채팅방 퇴장
    @MessageMapping("/room/{roomId}/leave")
    public void leaveChatRoom(@DestinationVariable Long roomId, @Payload String data, Principal principal)
            throws JsonProcessingException {
        String res = chatRoomService.leaveChatRoom(roomId, withLoginUser(data, "userId", principal));
        messagingTemplate.convertAndSend("/pub/room/" + roomId, res);
    }

    @MessageMapping("/join/{roomId}")
    public void join(@DestinationVariable Long roomId,
                     @Payload String data, Principal principal) throws JsonProcessingException {
        log.info("채팅방 재 입장");
        String res = chatRoomService.joinChatRoom(roomId, withLoginUser(data, "userId", principal));
        messagingTemplate.convertAndSend("/pub/room/" + roomId, res);
    }

    //dm 참여 및 메세지 전송
    @MessageMapping("/dm/{user1Id}/{user2Id}")
    public void createChatOrSend(@DestinationVariable("user1Id") Long user1Id,
                                 @DestinationVariable("user2Id") Long user2Id,
                                 @Payload String payload, Principal principal) throws JsonProcessingException {
        validateDmMember(user1Id, user2Id, principal);
        payload = withLoginUser(payload, "senderId", principal);
        Map<String, Object> sendData = user1Id.compareTo(user2Id) < 0
                                       ? chatDmService.createChatOrSend(user1Id, user2Id, payload)
                                       : chatDmService.createChatOrSend(user2Id, user1Id, payload);
        messagingTemplate.convertAndSend("/pub/dm/" + user1Id + "/" + user2Id,
                                         mapper.writeValueAsString(sendData));
    }

    //dm떠나기
    @MessageMapping("/dm/{user1Id}/{user2Id}/leave")
    public void leaveDm(@DestinationVariable("user1Id") Long user1Id,
                        @DestinationVariable("user2Id") Long user2Id,
                        @Payload String payload, Principal principal) throws JsonProcessingException {
        validateDmMember(user1Id, user2Id, principal);
        payload = withLoginUser(payload, "leaveUserId", principal);
        String sendData = user1Id.compareTo(user2Id) < 0
                          ? chatDmService.leaveDm(user1Id, user2Id, payload)
                          : chatDmService.leaveDm(user2Id, user1Id, payload);
        messagingTemplate.convertAndSend("/pub/dm/" + user1Id + "/" + user2Id, sendData);
    }

    // 메시지 본문의 사용자 ID를 신뢰하지 않고, STOMP CONNECT 시 토큰으로 인증된 사용자로 덮어쓴다.
    private String withLoginUser(String payload, String field, Principal principal) throws JsonProcessingException {
        ObjectNode node = (ObjectNode) mapper.readTree(payload);
        node.put(field, Long.valueOf(principal.getName()));
        return mapper.writeValueAsString(node);
    }

    private void validateDmMember(Long user1Id, Long user2Id, Principal principal) {
        Long loginUserId = Long.valueOf(principal.getName());
        if (!loginUserId.equals(user1Id) && !loginUserId.equals(user2Id)) {
            throw new IllegalArgumentException("DM 참여자만 메시지를 보낼 수 있습니다.");
        }
    }
}
