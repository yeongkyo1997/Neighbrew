package com.ssafy.backend.domain.meet.service.meet.manager;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.repository.ChatRoomRepository;
import com.ssafy.backend.domain.chat.service.room.ChatRoomService;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomUserManager;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetChatManager {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserManager chatRoomUserManager;
    private final ChatRoomService chatRoomService;
    private final MongoTemplate mongoTemplate;

    public ChatRoom createChatRoom(String meetName) {
        return chatRoomRepository.save(ChatRoom.builder().chatRoomName(meetName + "모임의 채팅방").build());
    }

    public void setupChatRoomUser(ChatRoom chatRoom, User hostUser) {
        settingChatRoomUser(chatRoom, hostUser, hostUser, "채팅방이 생성되었습니다.");
    }

    public void addUserToChatRoom(ChatRoom chatRoom, User user, User host, String nickname) {
        settingChatRoomUser(chatRoom, user, host, nickname + "님이 모임에 참여하셨습니다.");
    }

    public void removeUserFromChatRoom(ChatRoom chatRoom, Long userId) {
        chatRoomService.removeUserFromChatRoom(chatRoom, userId);
    }

    public void deleteChatRoomIfEmpty(Meet deleteMeet, boolean isOnlyHost) {
        if (isOnlyHost) {
            chatRoomRepository.deleteById(deleteMeet.getChatRoom().getChatRoomId());
        }
    }

    // member를 채팅방에 추가하고, messageUser 명의의 시스템 메시지를 남긴다.
    private void settingChatRoomUser(ChatRoom chatRoom, User member, User messageUser, String message) {
        chatRoomUserManager.addUserToRoom(chatRoom, member);

        mongoTemplate.insert(ChatMessageMongo.builder().chatRoomId(chatRoom.getChatRoomId()).message(message)
                                             .createdAt(String.valueOf(LocalDateTime.now()))
                                             .userId(messageUser.getUserId()).build(), "chat");
    }
}