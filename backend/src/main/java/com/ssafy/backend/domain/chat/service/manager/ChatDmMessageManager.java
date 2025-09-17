package com.ssafy.backend.domain.chat.service.manager;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatDmMessage;
import com.ssafy.backend.domain.chat.entity.ChatDmRoom;
import com.ssafy.backend.domain.chat.repository.ChatDmMessageRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatDmMessageManager {
    private final ChatDmMessageRepository chatDmMessageRepository;

    public void saveChatMessage(ChatDmRoom chatRoom, User sender, String message) {
        chatDmMessageRepository.save(ChatDmMessage.builder().chatDmRoom(chatRoom).user(sender).message(message)
                                                  .createdAt(LocalDateTime.now()).build());
    }

    public void deleteMessagesByRoomId(Long chatRoomId) {
        chatDmMessageRepository.deleteByChatDmRoomChatDmRoomId(chatRoomId);
    }
}