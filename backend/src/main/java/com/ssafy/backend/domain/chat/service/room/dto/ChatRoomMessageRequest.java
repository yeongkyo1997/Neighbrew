package com.ssafy.backend.domain.chat.service.room.dto;

public class ChatRoomMessageRequest {
    public final Long userId;
    public final String message;

    public ChatRoomMessageRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}