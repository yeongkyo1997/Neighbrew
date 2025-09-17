package com.ssafy.backend.domain.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageMongo, String> {
    List<ChatMessageMongo> findByChatRoomIdOrderByCreatedAt(Long chatRoomId);
}
