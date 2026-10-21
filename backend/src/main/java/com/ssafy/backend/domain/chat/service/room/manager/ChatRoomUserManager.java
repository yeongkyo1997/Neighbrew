package com.ssafy.backend.domain.chat.service.room.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.entity.ChatRoomUser;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomUserManager {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final MeetUserRepository meetUserRepository;

    public void addUsersToRoom(ChatRoom chatRoom, List<Integer> userIdList) {
        userIdList.forEach(userId -> {
            User user = findUserById(Long.valueOf(userId));
            chatRoomUserRepository.save(ChatRoomUser.builder().chatRoom(chatRoom).user(user).build());
        });
    }

    public List<User> getUsersInChatRoom(Long chatRoomId) {
        return chatRoomUserRepository.findByChatRoomChatRoomId(chatRoomId).stream().map(ChatRoomUser::getUser)
                                     .collect(Collectors.toList());
    }

    public boolean isUserInChatRoom(Long chatRoomId, Long userId) {
        return chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(chatRoomId, userId);
    }

    // 채팅방 참여자이거나, 채팅방이 속한 모임의 방장/참여자면 허용한다.
    // (모임 멤버는 재입장(/sub/join) 처리 전에 구독·조회해도 막히지 않도록)
    public boolean isRoomMember(Long chatRoomId, Long userId) {
        if (isUserInChatRoom(chatRoomId, userId)) {
            return true;
        }
        Status meetStatus = meetUserRepository.findStatusByUserIdAndChatRoomId(userId, chatRoomId);
        return meetStatus == Status.HOST || meetStatus == Status.GUEST;
    }

    public void requireRoomMember(Long chatRoomId, Long userId) {
        if (!isRoomMember(chatRoomId, userId)) {
            throw new ForbiddenException("참여중인 채팅방만 조회할 수 있습니다.");
        }
    }

    public void addUserToRoom(ChatRoom chatRoom, User user) {
        chatRoomUserRepository.save(ChatRoomUser.builder().chatRoom(chatRoom).user(user).build());
    }

    public void removeUserFromRoom(Long chatRoomId, Long userId) {
        chatRoomUserRepository.deleteByUserUserIdAndChatRoomChatRoomId(userId, chatRoomId);
    }

    public boolean isUserAlreadyInRoom(ChatRoom chatRoom, Long userId) {
        return chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(chatRoom.getChatRoomId(), userId);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}