package com.ssafy.backend.domain.chat.service.room.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.entity.ChatRoomUser;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomUserManager {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;

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