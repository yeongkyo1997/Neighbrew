package com.ssafy.backend.domain.chat.service.room.manager;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.repository.ChatMessageMongoRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomMessageManager {
    private final ChatMessageMongoRepository chatMessageMongoRepository;
    private final MongoTemplate mongoTemplate;

    public void saveSystemMessage(Long chatRoomId, String message) {
        chatMessageMongoRepository.save(ChatMessageMongo.builder().chatRoomId(chatRoomId).message(message)
                                                        .createdAt(String.valueOf(LocalDateTime.now()))
                                                        .build());
    }

    public ChatMessageMongo saveUserMessage(Long chatRoomId, User user, String message) {
        ChatMessageMongo chatMessageMongo = ChatMessageMongo.builder().chatRoomId(chatRoomId).message(message)
                                                            .userId(user.getUserId()).userNickname(
                        user.getNickname()).createdAt(String.valueOf(LocalDateTime.now())).build();

        return mongoTemplate.insert(chatMessageMongo);
    }

    public ChatMessageMongo createUserJoinMessage(Long chatRoomId, User user) {
        String message = user.getNickname() + "님께서 다시 채팅방에 참여하셨습니다.";
        return chatMessageMongoRepository.save(ChatMessageMongo.builder().chatRoomId(chatRoomId).message(
                message).createdAt(LocalDateTime.now().toString()).build());
    }

    public ChatMessageMongo createUserLeaveMessage(Long chatRoomId, User user) {
        String message = user.getNickname() + "님이 채팅방을 나갔습니다.";
        return mongoTemplate.insert(ChatMessageMongo.builder().userId(user.getUserId()).message(message)
                                                    .chatRoomId(chatRoomId)
                                                    .createdAt(String.valueOf(LocalDateTime.now())).build());
    }
}