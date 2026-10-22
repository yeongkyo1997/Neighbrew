package com.ssafy.backend.domain.chat.service.manager;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.chat.repository.ChatDmRoomRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.exception.NotFoundException;

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
                () -> new NotFoundException("채팅방을 찾을 수 없습니다. users=" + user1Id + "," + user2Id));
    }

    public void updateChatRoomAttendTimes(ChatDmRoom chatRoom, User sender, LocalDateTime currentTime) {
        // 보낸 사람이 방의 참여자면, 나갔던 상대방을 포함해 두 사람 모두 다시 입장 처리한다.
        if (isSenderUser1(chatRoom, sender) || isSenderUser2(chatRoom, sender)) {
            ensureUser1Attendance(chatRoom, currentTime);
            ensureUser2Attendance(chatRoom, currentTime);
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

    // 요청한 유저가 방의 user1인지 user2인지에 따라 해당 유저의 입장 시각을 반환
    public LocalDateTime getAttendTimeForUser(ChatDmRoom dmRoom, Long requestUserId) {
        return requestUserId.equals(dmRoom.getUser1().getUserId()) ? dmRoom.getUser1AttendTime()
                                                                   : dmRoom.getUser2AttendTime();
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