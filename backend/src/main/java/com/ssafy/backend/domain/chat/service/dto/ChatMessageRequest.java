package com.ssafy.backend.domain.chat.service.dto;

public class ChatMessageRequest {
    public final String message;
    public final Long senderId;

    public ChatMessageRequest(String message, Long senderId) {
        this.message = message;
        this.senderId = senderId;
    }
}