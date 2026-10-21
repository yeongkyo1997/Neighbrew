package com.ssafy.backend.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.repository.ChatMessageMongoRepository;
import com.ssafy.backend.domain.chat.service.room.manager.ChatRoomUserManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageMongoRepository chatMessageMongoRepository;
    private final ChatRoomUserManager chatRoomUserManager;

    public List<ChatMessageMongo> getChatMessages(Long chatRoomId, Long userId) {
        chatRoomUserManager.requireRoomMember(chatRoomId, userId);
        return chatMessageMongoRepository.findByChatRoomIdOrderByCreatedAt(chatRoomId);
    }
}
