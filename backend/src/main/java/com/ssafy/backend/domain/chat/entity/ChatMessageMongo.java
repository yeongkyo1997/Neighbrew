package com.ssafy.backend.domain.chat.entity;

import jakarta.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "chat")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatMessageMongo {
    @Id
    private String id;
    private Long chatRoomId;
    private Long userId;
    private String userNickname;
    private String message;
    private String createdAt;
}
