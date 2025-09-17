package com.ssafy.backend.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.repository.ChatMessageMongoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageMongoRepository chatMessageMongoRepository;

    public List<ChatMessageMongo> getChatMessages(Long chatRoomId) {
        return chatMessageMongoRepository.findByChatRoomIdOrderByCreatedAt(chatRoomId);
    }
}
