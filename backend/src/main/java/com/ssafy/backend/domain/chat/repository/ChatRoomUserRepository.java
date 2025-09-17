package com.ssafy.backend.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.chat.entity.ChatRoomUser;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    Optional<ChatRoomUser> findByChatRoomChatRoomIdAndUserUserId(Long chatRoomId, Long userId);

    List<ChatRoomUser> findByUserUserId(Long userId);

    List<ChatRoomUser> findByChatRoomChatRoomId(Long chatRoomId);

    void deleteByUserUserIdAndChatRoomChatRoomId(Long userId, Long roomId);
}
