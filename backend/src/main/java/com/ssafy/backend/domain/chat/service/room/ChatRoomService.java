package com.ssafy.backend.domain.chat.service.room;

import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomCreateRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomJoinRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomLeaveRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomMessageRequest;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomManager;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomMessageManager;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomUserManager;
import com.ssafy.backend.domain.chat.service.room.validator.ChatRoomValidator;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomValidator validator;
    private final ChatRoomManager roomManager;
    private final ChatRoomUserManager userManager;
    private final ChatRoomMessageManager messageManager;
    private final PushService pushService;
    private final MeetUserRepository meetUserRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

    public List<ChatRoom> findUserChatRooms(Long userId) {
        return roomManager.findUserChatRooms(userId);
    }

    @Transactional
    public ChatRoom createChatRoom(Map<String, Object> map) {
        ChatRoomCreateRequest request = validator.parseCreateRequest(map);

        ChatRoom room = roomManager.createChatRoom(request);

        messageManager.saveSystemMessage(room.getChatRoomId(), "채팅방이 생성되었습니다.");

        userManager.addUsersToRoom(room, request.userIdList);

        return room;
    }

    public String sendChatRoomMessage(Long roomId, String data) throws JsonProcessingException {
        ChatRoomMessageRequest request = validator.parseMessageRequest(data);

        ChatRoom chatRoom = roomManager.findChatRoomById(roomId);
        User user = userManager.findUserById(request.userId);

        if (!userManager.isUserInChatRoom(chatRoom.getChatRoomId(), request.userId)) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 유저입니다.");
        }

        messageManager.saveUserMessage(chatRoom.getChatRoomId(), user, request.message);

        return createMessageResponse(request, user);
    }

    private String createMessageResponse(ChatRoomMessageRequest request, User user)
            throws JsonProcessingException {
        Map<String, Object> response = Map.of("userId", request.userId, "message", request.message,
                                              "userNickname", user.getNickname());
        return mapper.writeValueAsString(response);
    }

    @Transactional
    public String leaveChatRoom(Long roomId, String data) throws JsonProcessingException {
        ChatRoomLeaveRequest request = validator.parseLeaveRequest(data);

        ChatRoom room = roomManager.findChatRoomById(roomId);
        User user = userManager.findUserById(request.userId);

        userManager.removeUserFromRoom(room.getChatRoomId(), request.userId);
        ChatMessageMongo leaveMessage = messageManager.createUserLeaveMessage(room.getChatRoomId(), user);

        return mapper.writeValueAsString(leaveMessage);
    }

    public List<User> getUsersInChatRoom(Long chatRoomId) {
        return userManager.getUsersInChatRoom(chatRoomId);
    }

    public ChatRoom getChatRoomDetail(Long chatRoomId) {
        return roomManager.getChatRoomDetail(chatRoomId);
    }

    @Transactional
    public void removeUserFromChatRoom(ChatRoom chatRoom, Long userId) {
        User user = userManager.findUserById(userId);
        userManager.removeUserFromRoom(chatRoom.getChatRoomId(), userId);
        messageManager.createUserLeaveMessage(chatRoom.getChatRoomId(), user);
    }

    @Transactional
    public String joinChatRoom(Long roomId, String data) throws JsonProcessingException {
        ChatRoomJoinRequest request = validator.parseJoinRequest(data);

        User joinUser = userManager.findUserById(request.userId);
        ChatRoom chatRoom = roomManager.findChatRoomById(roomId);

        // 모임 멤버(방장/참여자)만 모임 채팅방에 (재)입장할 수 있다.
        Status meetStatus = meetUserRepository.findStatusByUserIdAndChatRoomId(request.userId, roomId);
        if (meetStatus != Status.HOST && meetStatus != Status.GUEST) {
            throw new IllegalArgumentException("모임에 참여중인 유저만 채팅방에 입장할 수 있습니다.");
        }

        if (userManager.isUserAlreadyInRoom(chatRoom, request.userId)) {
            return "";
        }

        userManager.addUserToRoom(chatRoom, joinUser);
        ChatMessageMongo joinMessage = messageManager.createUserJoinMessage(roomId, joinUser);

        return mapper.writeValueAsString(joinMessage);
    }
}
