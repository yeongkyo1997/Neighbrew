package com.ssafy.backend.domain.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.chat.entity.ChatDmMessage;

@Repository
public interface ChatDmMessageRepository extends JpaRepository<ChatDmMessage, Long> {
    void deleteByChatDmRoomChatDmRoomId(Long dmRoomId);

    List<ChatDmMessage> findByChatDmRoomChatDmRoomIdAndCreatedAtBetween(Long chatDmRoomId, LocalDateTime start,
                                                                        LocalDateTime end);
}
