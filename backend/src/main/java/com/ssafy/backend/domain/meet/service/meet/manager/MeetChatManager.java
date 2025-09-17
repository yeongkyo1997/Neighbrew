package com.ssafy.backend.domain.meet.service.meet.manager;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.entity.ChatRoomUser;
import com.ssafy.backend.domain.chat.repository.ChatRoomRepository;
import com.ssafy.backend.domain.chat.service.ChatRoomUserService;
import com.ssafy.backend.domain.chat.service.room.ChatRoomService;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetChatManager {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserService chatRoomUserService;
    private final ChatRoomService chatRoomService;
    private final MongoTemplate mongoTemplate;

    public ChatRoom createChatRoom(String meetName) {
        return chatRoomRepository.save(ChatRoom.builder().chatRoomName(meetName + "모임의 채팅방").build());
    }

    public void setupChatRoomUser(ChatRoom chatRoom, User hostUser) {
        settingChatRoomUser(ChatRoomUser.builder().chatRoom(chatRoom).user(hostUser).build(), chatRoom,
                            hostUser, "채팅방이 생성되었습니다.");
    }

    public void addUserToChatRoom(ChatRoom chatRoom, User user, User host, String nickname) {
        settingChatRoomUser(ChatRoomUser.builder().user(user).chatRoom(chatRoom).build(), chatRoom, host,
                            nickname + "님이 모임에 참여하셨습니다.");
    }

    public void removeUserFromChatRoom(ChatRoom chatRoom, Long userId) {
        chatRoomService.removeUserFromChatRoom(chatRoom, userId);
    }

    public void deleteChatRoomIfEmpty(Meet deleteMeet, boolean isOnlyHost) {
        if (isOnlyHost) {
            chatRoomRepository.deleteById(deleteMeet.getChatRoom().getChatRoomId());
        }
    }

    private void settingChatRoomUser(ChatRoomUser createChatRoom, ChatRoom chatRoom, User user,
                                     String message) {
        chatRoomUserService.save(createChatRoom);

        mongoTemplate.insert(ChatMessageMongo.builder().chatRoomId(chatRoom.getChatRoomId()).message(message)
                                             .createdAt(String.valueOf(LocalDateTime.now()))
                                             .userId(user.getUserId()).build(), "chat");
    }
}