package com.ssafy.backend.domain.chat.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.chat.entity.ChatDmMessage;
import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.chat.repository.ChatDmMessageRepository;
import com.ssafy.backend.domain.chat.repository.ChatDmRoomRepository;
import com.ssafy.backend.domain.chat.service.dto.ChatLeaveRequest;
import com.ssafy.backend.domain.chat.service.dto.ChatMessageRequest;
import com.ssafy.backend.domain.chat.service.manager.ChatDmMessageManager;
import com.ssafy.backend.domain.chat.service.manager.ChatDmRoomManager;
import com.ssafy.backend.domain.chat.service.validator.ChatDmValidator;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.dto.UserResponseDto;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatDmService {
    private final ChatDmMessageRepository chatDmMessageRepository;
    private final ChatDmRoomRepository chatDmRoomRepository;
    private final UserRepository userRepository;
    private final PushService pushService;
    private final ChatDmValidator validator;
    private final ChatDmRoomManager roomManager;
    private final ChatDmMessageManager messageManager;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

    //DM 목록 조회
    public List<ChatDmRoom> findMyDmList(Long userId) {
        return chatDmRoomRepository.findChatDmRoomByIdOrderByLastMessageTimeDesc(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")).stream().filter(
                cdr -> (cdr.getUser1().getUserId().equals(userId) && cdr.getUser1AttendTime() != null) || (
                        cdr.getUser2().getUserId().equals(userId) && cdr.getUser2AttendTime() != null)).collect(
                Collectors.toList());
    }

    public Map<String, Object> findDmMessagesByRoomId(Long user1Id, Long user2Id, Long requestUserId) {
        Map<String, Object> result = new HashMap<>();

        chatDmRoomRepository.findByUser1UserIdAndUser2UserId(user1Id, user2Id).ifPresent(dmRoom -> {
            LocalDateTime attendTime = roomManager.getAttendTimeForUser(dmRoom, requestUserId);
            List<ChatDmMessage> messages =
                    chatDmMessageRepository.findByChatDmRoomChatDmRoomIdAndCreatedAtBetween(
                            dmRoom.getChatDmRoomId(), attendTime, LocalDateTime.now());
            result.put("messages", messages);
        });

        result.put("user1", getUserResponseDto(user1Id));
        result.put("user2", getUserResponseDto(user2Id));

        return result;
    }

    @Transactional
    public Map<String, Object> createChatOrSend(Long user1Id, Long user2Id, String payload)
            throws JsonProcessingException {
        ChatMessageRequest request = validator.parseMessageRequest(payload);

        User user1 = findUserById(user1Id);
        User user2 = findUserById(user2Id);

        ChatDmRoom chatDmRoom = roomManager.findOrCreateChatRoom(user1, user2);

        User sender = request.senderId.equals(user1Id) ? user1 : user2;
        User receiver = request.senderId.equals(user1Id) ? user2 : user1;

        return saveMessageAndPush(chatDmRoom, sender, receiver, request.message);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private UserResponseDto getUserResponseDto(Long userId) {
        return UserResponseDto.fromEntity(findUserById(userId));
    }

    @Transactional
    public String leaveDm(Long user1Id, Long user2Id, String payload) throws JsonProcessingException {
        ChatLeaveRequest request = validator.parseLeaveRequest(payload);

        ChatDmRoom chatRoom = roomManager.findChatRoom(user1Id, user2Id);

        if (roomManager.shouldDeleteRoom(chatRoom, user1Id, user2Id, request.leaveUserId)) {
            return deleteRoomAndReturnMessage(chatRoom);
        }

        roomManager.updateUserAttendTime(chatRoom, user1Id, user2Id, request.leaveUserId);
        roomManager.saveChatRoom(chatRoom);

        return createLeaveMessage(request.leaveUserId);
    }

    private String deleteRoomAndReturnMessage(ChatDmRoom chatRoom) throws JsonProcessingException {
        messageManager.deleteMessagesByRoomId(chatRoom.getChatDmRoomId());
        roomManager.deleteChatRoom(chatRoom.getChatDmRoomId());
        return mapper.writeValueAsString(Collections.singletonMap("message", "모든 유저가 채팅방을 떠났습니다. 채팅을 종료합니다."));
    }

    private String createLeaveMessage(Long leaveUserId) throws JsonProcessingException {
        User leaveUser = findUserById(leaveUserId);
        String leaveMessage = leaveUser.getNickname() + "님이 채팅방을 나갔습니다.";

        Map<String, Object> result = new HashMap<>();
        result.put("message", leaveMessage);
        result.put("userId", leaveUserId);
        result.put("user", UserResponseDto.fromEntity(leaveUser));
        return mapper.writeValueAsString(result);
    }

    @Transactional
    public Map<String, Object> saveMessageAndPush(ChatDmRoom chatDmRoom, User sender, User receiver,
                                                  String message) {
        LocalDateTime currentTime = LocalDateTime.now();

        roomManager.updateChatRoomAttendTimes(chatDmRoom, sender, currentTime);
        chatDmRoom.setLastMessageTime(currentTime);
        roomManager.saveChatRoom(chatDmRoom);

        messageManager.saveChatMessage(chatDmRoom, sender, message);
        sendPushNotification(sender, receiver);

        return createMessageResponse(sender, message);
    }

    private void sendPushNotification(User sender, User receiver) {
        String pushMessage = sender.getNickname() + "님께서 메세지를 보냈습니다.";
        String pushUrl = neighbrewUrl + "/directchat/" + sender.getUserId() + "/" + receiver.getUserId();
        pushService.send(sender, receiver, PushType.DM, pushMessage, pushUrl);
    }

    private Map<String, Object> createMessageResponse(User sender, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("userId", sender.getUserId());
        result.put("user", UserResponseDto.fromEntity(sender));
        return result;
    }
}
