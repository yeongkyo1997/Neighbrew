package com.ssafy.backend.domain.chat.service.manager;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.chat.repository.ChatDmRoomRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatDmRoomManager {
    private final ChatDmRoomRepository chatDmRoomRepository;

    public ChatDmRoom findOrCreateChatRoom(User user1, User user2) {
        return chatDmRoomRepository.findDmUsers(user1, user2).orElseGet(
                () -> chatDmRoomRepository.save(ChatDmRoom.builder().user1(user1).user2(user2).build()));
    }

    public ChatDmRoom findChatRoom(Long user1Id, Long user2Id) {
        return chatDmRoomRepository.findByUser1UserIdAndUser2UserId(user1Id, user2Id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    }

    public void updateChatRoomAttendTimes(ChatDmRoom chatRoom, User sender, LocalDateTime currentTime) {
        if (isSenderUser1(chatRoom, sender)) {
            ensureUser1Attendance(chatRoom, currentTime);
            ensureUser2Attendance(chatRoom, currentTime);
        } else if (isSenderUser2(chatRoom, sender)) {
            ensureUser2Attendance(chatRoom, currentTime);
            ensureUser1Attendance(chatRoom, currentTime);
        }
    }

    public boolean shouldDeleteRoom(ChatDmRoom chatRoom, Long user1Id, Long user2Id, Long leaveUserId) {
        return (user1Id.equals(leaveUserId) && chatRoom.getUser2AttendTime() == null) || (user2Id.equals(
                leaveUserId) && chatRoom.getUser1AttendTime() == null);
    }

    public void updateUserAttendTime(ChatDmRoom chatRoom, Long user1Id, Long user2Id, Long leaveUserId) {
        if (user1Id.equals(leaveUserId)) {
            chatRoom.setUser1AttendTime(null);
        } else if (user2Id.equals(leaveUserId)) {
            chatRoom.setUser2AttendTime(null);
        }
    }

    public LocalDateTime getAttendTimeForUser(ChatDmRoom dmRoom, Long user1Id, Long user2Id) {
        return user2Id.equals(user1Id) ? dmRoom.getUser1AttendTime() : dmRoom.getUser2AttendTime();
    }

    public void saveChatRoom(ChatDmRoom chatRoom) {
        chatDmRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(Long chatRoomId) {
        chatDmRoomRepository.deleteById(chatRoomId);
    }

    private boolean isSenderUser1(ChatDmRoom chatRoom, User sender) {
        return Objects.equals(sender.getUserId(), chatRoom.getUser1().getUserId());
    }

    private boolean isSenderUser2(ChatDmRoom chatRoom, User sender) {
        return Objects.equals(sender.getUserId(), chatRoom.getUser2().getUserId());
    }

    private void ensureUser1Attendance(ChatDmRoom chatRoom, LocalDateTime currentTime) {
        if (chatRoom.getUser1AttendTime() == null) {
            chatRoom.setUser1AttendTime(currentTime);
        }
    }

    private void ensureUser2Attendance(ChatDmRoom chatRoom, LocalDateTime currentTime) {
        if (chatRoom.getUser2AttendTime() == null) {
            chatRoom.setUser2AttendTime(currentTime);
        }
    }
}